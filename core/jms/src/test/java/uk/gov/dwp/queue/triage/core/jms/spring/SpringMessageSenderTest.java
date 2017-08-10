package uk.gov.dwp.queue.triage.core.jms.spring;

import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SpringMessageSenderTest {

    private final JmsTemplate jmsTemplate = mock(JmsTemplate.class);
    private final FailedMessageCreator failedMessageCreator = mock(FailedMessageCreator.class);
    private final FailedMessage failedMessage = mock(FailedMessage.class);

    private final SpringMessageSender underTest = new SpringMessageSender(
            failedMessage -> failedMessageCreator,
            jmsTemplate
    );

    @Test
    public void successfullySendMessage() throws Exception {
        underTest.send(failedMessage);

        verify(jmsTemplate).send(failedMessageCreator);
    }
}