package uk.gov.dwp.queue.triage.core.classification.action;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteMessageActionTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();
    private final ObjectMapper objectMapper = new JacksonConfiguration().objectMapper();

    private final FailedMessageService failedMessageService = mock(FailedMessageService.class);
    private final FailedMessage failedMessage = mock(FailedMessage.class);

    @Before
    public void setup() {
        objectMapper.setInjectableValues(
                new InjectableValues.Std()
                        .addValue(FailedMessageService.class, failedMessageService)
        );
    }

    @Test
    public void canSerialiseAndDeserialiseAction() throws Exception {
        DeleteMessageAction underTest = objectMapper.readValue(
                objectMapper.writeValueAsString(new DeleteMessageAction(failedMessageService)),
                DeleteMessageAction.class
        );
        when(failedMessage.getFailedMessageId()).thenReturn(FAILED_MESSAGE_ID);

        underTest.accept(failedMessage);

        verify(failedMessageService).delete(FAILED_MESSAGE_ID);
    }
}