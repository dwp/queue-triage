package uk.gov.dwp.queue.triage.core.classification.classifier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.classification.action.FailedMessageAction;
import uk.gov.dwp.queue.triage.core.classification.client.MessageClassificationOutcomeResponse;
import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class MessageClassificationOutcomeAdapterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private Description<String> description;
    @Mock
    private MessageClassificationOutcome<String> outcome;
    @Mock
    private FailedMessageResponseFactory failedMessageResponseFactory;
    @Mock
    private FailedMessage failedMessage;
    @Mock
    private FailedMessageAction failedMessageAction;
    @Mock
    private FailedMessageResponse failedMessageResponse;

    private MessageClassificationOutcomeAdapter underTest;

    @Before
    public void setUp() {
        underTest = new MessageClassificationOutcomeAdapter(objectMapper, failedMessageResponseFactory);
    }

    @Test
    public void convertOutcomeToOutcomeResponse() throws JsonProcessingException {
        when(outcome.isMatched()).thenReturn(true);
        when(outcome.getFailedMessage()).thenReturn(failedMessage);
        when(outcome.getDescription()).thenReturn(description);
        when(description.getOutput()).thenReturn("Description");
        when(outcome.getFailedMessageAction()).thenReturn(failedMessageAction);
        when(objectMapper.writeValueAsString(failedMessageAction)).thenReturn("Action");
        when(failedMessageResponseFactory.create(failedMessage)).thenReturn(failedMessageResponse);

        final MessageClassificationOutcomeResponse response = underTest.toOutcomeResponse(outcome);

        assertThat(response.isMatched(), is(true));
        assertThat(response.getFailedMessageResponse(), is(failedMessageResponse));
        assertThat(response.getAction(), is("Action"));
        assertThat(response.getDescription(), is("Description"));
    }

    @Test
    public void actionIsClassNameWhenJsonProcessingExceptionIsThrown() throws JsonProcessingException {
        failedMessageAction = new StubFailedMessageAction();
        when(outcome.isMatched()).thenReturn(true);
        when(outcome.getFailedMessage()).thenReturn(failedMessage);
        when(outcome.getDescription()).thenReturn(description);
        when(description.getOutput()).thenReturn("Description");
        when(outcome.getFailedMessageAction()).thenReturn(failedMessageAction);
        when(objectMapper.writeValueAsString(failedMessageAction)).thenThrow(JsonProcessingException.class);
        when(failedMessageResponseFactory.create(failedMessage)).thenReturn(failedMessageResponse);

        final MessageClassificationOutcomeResponse response = underTest.toOutcomeResponse(outcome);

        assertThat(response.isMatched(), is(true));
        assertThat(response.getFailedMessageResponse(), is(failedMessageResponse));
        assertThat(response.getAction(), is("StubFailedMessageAction"));
        assertThat(response.getDescription(), is("Description"));
    }

    static class StubFailedMessageAction implements FailedMessageAction {

        @Override
        public void accept(FailedMessage failedMessage) {

        }
    }
}