package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifierGroup.newClassifierCollection;

public class DelegatingMessageClassifierTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private MessageClassificationContext context;
    @Mock
    private FailedMessage failedMessage;
    @Mock
    private FailedMessagePredicate predicate;
    @Mock
    private MessageClassifierGroup messageClassifierGroup;
    @Mock
    private MessageClassificationOutcome outcome;

    private DelegatingMessageClassifier underTest;

    @Before
    public void setUp() {
        underTest = new DelegatingMessageClassifier(predicate, messageClassifierGroup);
    }

    @Test
    public void predicateIsNotMatched() {
        when(context.getFailedMessage()).thenReturn(failedMessage);
        when(predicate.test(failedMessage)).thenReturn(false);
        when(context.notMatched(predicate)).thenReturn(outcome);

        assertThat(underTest.classify(context), is(outcome));

        verify(predicate).test(failedMessage);
        verify(context).notMatched(predicate);
        verifyZeroInteractions(messageClassifierGroup);
    }

    @Test
    public void predicateIsMatched() {
        when(context.getFailedMessage()).thenReturn(failedMessage);
        when(predicate.test(failedMessage)).thenReturn(true);
        MessageClassificationOutcome initialClassificationOutcome = mock(MessageClassificationOutcome.class);
        when(context.matched(predicate)).thenReturn(initialClassificationOutcome);
        MessageClassificationOutcome subsequentClassificationOutcome = mock(MessageClassificationOutcome.class);
        when(messageClassifierGroup.classify(context)).thenReturn(subsequentClassificationOutcome);
        when(initialClassificationOutcome.and(subsequentClassificationOutcome)).thenReturn(outcome);

        assertThat(underTest.classify(context), is(outcome));

        verify(predicate).test(failedMessage);
        verify(context).matched(predicate);
        verify(initialClassificationOutcome).and(subsequentClassificationOutcome);
    }

    @Test
    public void classifierCanBeSerialisedAndDeserialsed() throws JsonProcessingException {
        ObjectMapper objectMapper = JacksonConfiguration.defaultObjectMapper();
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

    @Test
    public void testToString() {
        when(predicate.toString()).thenReturn("predicate");
        when(messageClassifierGroup.toString()).thenReturn("more classifiers");
        assertThat(underTest.toString(), is("if predicate then more classifiers"));
    }
}