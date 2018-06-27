package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import uk.gov.dwp.queue.triage.core.classification.classifier.Description;
import uk.gov.dwp.queue.triage.core.classification.classifier.StringDescription;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class AndPredicateTest {

    private final FailedMessagePredicate alwaysTruePredicate = new BooleanPredicate(true);
    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final FailedMessagePredicate failedMessagePredicate1 = mock(FailedMessagePredicate.class);
    private final FailedMessagePredicate failedMessagePredicate2 = mock(FailedMessagePredicate.class);

    private final AndPredicate underTest = new AndPredicate(asList(failedMessagePredicate1, failedMessagePredicate2));

    @Test
    public void resultIsFalseWhen1stPredicateIsTrue2ndPredicteIsFalse() {
        when(failedMessagePredicate1.test(failedMessage)).thenReturn(true);
        when(failedMessagePredicate2.test(failedMessage)).thenReturn(false);

        assertThat(underTest.test(failedMessage), is(false));

        verify(failedMessagePredicate1).test(failedMessage);
        verify(failedMessagePredicate2).test(failedMessage);
    }

    @Test
    public void resultIsAlwaysFalseWhen1stPredicateIsFalse() {
        when(failedMessagePredicate1.test(failedMessage)).thenReturn(false);
        when(failedMessagePredicate2.test(failedMessage)).thenReturn(true);

        assertThat(underTest.test(failedMessage), is(false));

        verify(failedMessagePredicate1).test(failedMessage);
        verifyZeroInteractions(failedMessagePredicate2);
    }

    @Test
    public void resultIsTrueWhenAllPredicatesReturnTrue() {
        when(failedMessagePredicate1.test(failedMessage)).thenReturn(true);
        when(failedMessagePredicate2.test(failedMessage)).thenReturn(true);

        assertThat(underTest.test(failedMessage), is(true));

        verify(failedMessagePredicate1).test(failedMessage);
        verify(failedMessagePredicate2).test(failedMessage);
    }

    @Test
    public void canSerialiseAndDeserialisePredicate() throws IOException {
        ObjectMapper objectMapper = new JacksonConfiguration().objectMapper(new InjectableValues.Std());
        objectMapper.registerSubtypes(BooleanPredicate.class);

        final AndPredicate underTest = objectMapper.readValue(
                objectMapper.writeValueAsString(new AndPredicate(singletonList(alwaysTruePredicate))),
                AndPredicate.class
        );

        assertThat(underTest.getPredicates(), contains(alwaysTruePredicate));
    }

    @Test
    public void describeTest() {
        final Description description = mock(StringDescription.class, Mockito.RETURNS_SELF);

        underTest.describe(description);

        final InOrder orderVerifier = Mockito.inOrder(description, failedMessagePredicate1, failedMessagePredicate2);
        orderVerifier.verify(description).append("( ");
        orderVerifier.verify(failedMessagePredicate1).describe(description);
        orderVerifier.verify(description).append(" AND ");
        orderVerifier.verify(failedMessagePredicate2).describe(description);
        orderVerifier.verify(description).append(" )");
    }

    @Test
    public void toStringTest() {
        when(failedMessagePredicate1.describe(any(Description.class))).thenAnswer(withDescription("predicate1"));
        when(failedMessagePredicate2.describe(any(Description.class))).thenAnswer(withDescription("predicate2"));

        assertThat(underTest.toString(), is("( predicate1 AND predicate2 )"));
    }

    private Answer<Description> withDescription(String predicateDescription) {
        return invocation -> ((Description)invocation.getArgument(0)).append(predicateDescription);
    }
}