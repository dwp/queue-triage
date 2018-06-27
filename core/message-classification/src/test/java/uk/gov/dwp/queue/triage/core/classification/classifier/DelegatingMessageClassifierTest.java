package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.classification.action.DeleteMessageAction;
import uk.gov.dwp.queue.triage.core.classification.action.LabelMessageAction;
import uk.gov.dwp.queue.triage.core.classification.predicate.BrokerEqualsPredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.ContentContainsJsonPath;
import uk.gov.dwp.queue.triage.core.classification.predicate.DestinationEqualsPredicate;
import uk.gov.dwp.queue.triage.core.classification.predicate.FailedMessagePredicate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup.newClassifierCollection;

public class DelegatingMessageClassifierTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock(answer = Answers.RETURNS_SELF)
    private Description<String> description;
    @Mock
    private FailedMessage failedMessage;
    @Mock
    private FailedMessagePredicate failedMessagePredicate;
    @Mock
    private MessageClassifierGroup messageClassifierGroup;
    @Mock
    private MessageClassificationOutcome<String> messageClassificationOutcome;

    private DelegatingMessageClassifier underTest;

    @Before
    public void setUp() {
        underTest = new DelegatingMessageClassifier(failedMessagePredicate, messageClassifierGroup);
    }

    @Test
    public void predicateIsNotMatched() {
        when(failedMessagePredicate.describe(description)).thenReturn(description);
        when(failedMessagePredicate.test(failedMessage)).thenReturn(false);

        final MessageClassificationOutcome outcome = underTest.classify(failedMessage, description);

        assertThat(outcome.isMatched(), is(false));
        assertThat(outcome.getDescription(), is(description));
        verify(failedMessagePredicate).test(failedMessage);
        verify(failedMessagePredicate).describe(description);
        verify(description).append(" [");
        verify(description).append(false);
        verify(description).append("]");
        verifyZeroInteractions(messageClassifierGroup);
    }

    @Test
    public void predicateIsMatched() {
        when(failedMessagePredicate.describe(description)).thenReturn(description);
        when(failedMessagePredicate.test(failedMessage)).thenReturn(true);
        when(messageClassifierGroup.classify(failedMessage, description)).thenReturn(messageClassificationOutcome);

        final MessageClassificationOutcome outcome = underTest.classify(failedMessage, description);

        assertThat(outcome, is(messageClassificationOutcome));
        verify(failedMessagePredicate).test(failedMessage);
        verify(failedMessagePredicate).describe(description);
        verify(description).append(" [");
        verify(description).append(true);
        verify(description).append("]");
        verify(messageClassifierGroup).classify(failedMessage, description);
    }

    @Test
    public void classifierCanBeSerialisedAndDeserialsed() throws JsonProcessingException {
        ObjectMapper objectMapper = new JacksonConfiguration().objectMapper(new InjectableValues.Std());
        String json = objectMapper.writeValueAsString(new DelegatingMessageClassifier(
                new BrokerEqualsPredicate("internal-broker"),
                newClassifierCollection()
                        .withClassifier(new ExecutingMessageClassifier(
                                new DestinationEqualsPredicate(Optional.ofNullable("some-queue")),
                                new LabelMessageAction("foo", null)))
                        .withClassifier(new DelegatingMessageClassifier(
                                new DestinationEqualsPredicate(Optional.ofNullable("another-queue")),
                                newClassifierCollection()
                                        .withClassifier(new ExecutingMessageClassifier(
                                                new ContentContainsJsonPath("$.foo"),
                                                new DeleteMessageAction(null)))
                                        .build()))
                        .build()
        ));
        System.out.println(json);
    }
}