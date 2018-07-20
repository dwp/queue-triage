package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.classification.classifier.Description;
import uk.gov.dwp.queue.triage.core.classification.classifier.StringDescription;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public abstract class AbstractFailedMessagePredicateTest {

    private static final ObjectMapper OBJECT_MAPPER = JacksonConfiguration.defaultObjectMapper();

    protected final FailedMessage failedMessage = mock(FailedMessage.class);
    protected FailedMessagePredicate underTest;

    @BeforeClass
    public static void registerBooleanPredicate() {
        OBJECT_MAPPER.registerSubtypes(BooleanPredicate.class);
    }

    @Before
    public void assignPredicateUnderTest() {
        underTest = createPredicateUnderTest();
    }

    @Test
    public void canSerialiseAndDeserialisePredicate() throws IOException {

        String json = OBJECT_MAPPER.writeValueAsString(underTest);

        final FailedMessagePredicate failedMessagePredicate = OBJECT_MAPPER.readValue(json, FailedMessagePredicate.class);
        assertThat(
                underTest.getClass().getSimpleName() + " is not equal after serialisation/deserialisation",
                EqualsBuilder.reflectionEquals(underTest, failedMessagePredicate, excludedFieldsFromEquality()),
                is(true));
    }

    @Test
    public void describeTest() {
        final Description<String> description = underTest.describe(new StringDescription());

        assertThat(underTest.getClass().getSimpleName() + " description", description.getOutput(), expectedDescription());
    }

    @Test
    public void toStringTest() {
        assertThat(underTest.getClass().getSimpleName() + " toString", underTest.toString(), expectedDescription());
    }

    protected abstract Matcher<String> expectedDescription();

    protected abstract FailedMessagePredicate createPredicateUnderTest();

    protected String[] excludedFieldsFromEquality() {
        return new String[0];
    }
}
