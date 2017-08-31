package uk.gov.dwp.queue.triage.core.jms.spring;

import org.junit.Test;
import org.springframework.jms.core.JmsTemplate;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.jms.DestinationException;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(failedMessage.getDestination()).thenReturn(aDestination("some-destination"));

        underTest.send(failedMessage);

        verify(jmsTemplate).send("some-destination", failedMessageCreator);
    }

    @Test(expected = DestinationException.class)
    public void destinationIsMissing() {
        when(failedMessage.getDestination()).thenReturn(aDestination(null));

        underTest.send(failedMessage);
    }

    private Destination aDestination(String name) {
        return new Destination("some-broker", Optional.ofNullable(name));
    }
}