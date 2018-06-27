package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.classification.action.CustomActionRegistrationTest.GetPropertyAction;
import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.BooleanPredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static uk.gov.dwp.queue.triage.matchers.ReflectionEqualsMatcher.reflectionEquals;

public class ExecutingMessageClassifierTest {

    private final ObjectMapper objectMapper = new JacksonConfiguration().objectMapper(new InjectableValues.Std());

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private FailedMessage failedMessage;
    @Mock(answer = Answers.RETURNS_SELF)
    private Description<String> description;
    @Mock
    private FailedMessageAction failedMessageAction;
    @Mock
    private FailedMessagePredicate predicate;

    private MessageClassifier underTest;

    @Before
    public void setUp() {
        underTest = new ExecutingMessageClassifier(predicate, failedMessageAction);
    }

    @Test
    public void actionIsExecutedIfThePredicateIsTrue() {
        when(predicate.describe(description)).thenReturn(description);
        when(predicate.test(failedMessage)).thenReturn(true);

        final MessageClassificationOutcome outcome = underTest.classify(failedMessage, description);

        assertThat(outcome.isMatched(), is(true));
        assertThat(outcome.getDescription(), is(description));
        assertThat(outcome.getFailedMessage(), is(failedMessage));
        assertThat(outcome.getFailedMessageAction(), is(failedMessageAction));
        verify(predicate).describe(description);
        verify(description).append(" [");
        verify(description).append(true);
        verify(description).append("]");
    }

    @Test
    public void actionIsNotExecutedIfThePredicateIsFalse() {
        when(predicate.describe(description)).thenReturn(description);
        when(predicate.test(failedMessage)).thenReturn(false);

        final MessageClassificationOutcome outcome = underTest.classify(failedMessage, description);

        assertThat(outcome.isMatched(), is(false));
        assertThat(outcome.getDescription(), is(description));
        assertThat(outcome.getFailedMessage(), is(failedMessage));
        assertThat(outcome.getFailedMessageAction(), is(nullValue()));
        verify(predicate).describe(description);
        verify(description).append(" [");
        verify(description).append(false);
        verify(description).append("]");
    }

    @Test
    public void canSerialiseAndDeserialiseMessageClassifier() throws IOException {
        objectMapper.registerSubtypes(BooleanPredicate.class);
        objectMapper.registerSubtypes(GetPropertyAction.class);

        final ExecutingMessageClassifier underTest = new ExecutingMessageClassifier(
                new BooleanPredicate(true),
                new GetPropertyAction("foo")
        );

        final String json = objectMapper.writeValueAsString(underTest);

        assertThat(json, allOf(
                hasJsonPath("$._classifier", equalTo("executing")),
                hasJsonPath("$.predicate._type", equalTo("boolean")),
                hasJsonPath("$.predicate.result", equalTo(true)),
                hasJsonPath("$.action._action", equalTo("getProperty")),
                hasJsonPath("$.action.propertyName", equalTo("foo"))
        ));

        assertThat(objectMapper.readValue(json, ExecutingMessageClassifier.class), reflectionEquals(underTest));
    }
}