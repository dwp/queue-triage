package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static uk.gov.dwp.queue.triage.matchers.ReflectionEqualsMatcher.reflectionEquals;

public class ExecutingMessageClassifierTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private FailedMessage failedMessage;
    @Mock
    private MessageClassificationContext context;
    @Mock
    private MessageClassificationOutcome outcome;
    @Mock
    private FailedMessageAction action;
    @Mock
    private FailedMessagePredicate predicate;

    private MessageClassifier underTest;

    @Before
    public void setUp() {
        underTest = new ExecutingMessageClassifier(predicate, action);

    }

    @Test
    public void actionIsExecutedIfThePredicateIsTrue() {
        when(context.getFailedMessage()).thenReturn(failedMessage);
        when(predicate.test(failedMessage)).thenReturn(true);
        when(context.matched(predicate, action)).thenReturn(outcome);

        assertThat(underTest.classify(context), is(outcome));

        verify(predicate).test(failedMessage);
        verify(context).matched(predicate, action);
    }

    @Test
    public void actionIsNotExecutedIfThePredicateIsFalse() {
        when(context.getFailedMessage()).thenReturn(failedMessage);
        when(predicate.test(failedMessage)).thenReturn(false);
        when(context.notMatched(predicate)).thenReturn(outcome);

        assertThat(underTest.classify(context), is(outcome));

        verify(predicate).test(failedMessage);
        verify(context).notMatched(predicate);
    }

    @Test
    public void canSerialiseAndDeserialiseMessageClassifier() throws IOException {
        final ObjectMapper objectMapper = JacksonConfiguration.defaultObjectMapper();
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

    @Test
    public void testToString() {
        when(predicate.toString()).thenReturn("predicate");
        when(action.toString()).thenReturn("action");
        assertThat(underTest.toString(), is("if predicate then action"));
    }
}