package uk.gov.dwp.queue.triage.core.classification;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.classification.action.CustomActionRegistrationTest.GetPropertyAction;
import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.CustomPredicateRegistrationTest.BooleanPredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static uk.gov.dwp.queue.triage.matchers.ReflectionEqualsMatcher.reflectionEquals;

public class MessageClassifierTest {

    private final ObjectMapper objectMapper = new JacksonConfiguration().objectMapper();

    private FailedMessage failedMessage = mock(FailedMessage.class);
    private FailedMessagePredicate alwaysTrue = failedMessage -> true;
    private FailedMessagePredicate alwaysFalse = failedMessage -> false;
    private FailedMessageAction failedMessageAction = mock(FailedMessageAction.class);

    @Test
    public void actionIsExecutedIfThePredicateIsTrue() {
        MessageClassifier underTest = new MessageClassifier("Default Classifier", alwaysTrue, failedMessageAction);

        underTest.accept(failedMessage);

        verify(failedMessageAction).accept(failedMessage);
    }

    @Test
    public void actionIsNotExecutedIfThePredicateIsFalse() {
        MessageClassifier underTest = new MessageClassifier("Default Classifier", alwaysFalse, failedMessageAction);

        underTest.accept(failedMessage);

        verifyZeroInteractions(failedMessageAction);
    }

    @Test
    public void canSerialiseAndDeserialiseMessageClassifier() throws IOException {
        objectMapper.registerSubtypes(BooleanPredicate.class);
        objectMapper.registerSubtypes(GetPropertyAction.class);

        final MessageClassifier underTest = new MessageClassifier(
                "Some Description",
                new BooleanPredicate(true),
                new GetPropertyAction("foo")
        );

        final String json = objectMapper.writeValueAsString(underTest);

        assertThat(json, allOf(
                hasJsonPath("$.description", equalTo("Some Description")),
                hasJsonPath("$.predicate._type", equalTo("boolean")),
                hasJsonPath("$.predicate.result", equalTo(true)),
                hasJsonPath("$.action._action", equalTo("getProperty")),
                hasJsonPath("$.action.propertyName", equalTo("foo"))
        ));

        assertThat(objectMapper.readValue(json, MessageClassifier.class), reflectionEquals(underTest));
    }
}