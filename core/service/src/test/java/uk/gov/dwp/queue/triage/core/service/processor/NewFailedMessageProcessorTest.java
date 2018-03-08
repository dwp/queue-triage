package uk.gov.dwp.queue.triage.core.service.processor;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.service.FailedMessageService;

import static org.mockito.Mockito.mock;

public class NewFailedMessageProcessorTest {

    private final FailedMessageService failedMessageService = mock(FailedMessageService.class);
    private final NewFailedMessageProcessor underTest = new NewFailedMessageProcessor(failedMessageService);
    private final FailedMessage failedMessage = mock(FailedMessage.class);

    @Test
    public void delegatesToFailedMessageService() {
        underTest.process(failedMessage);
    }
}