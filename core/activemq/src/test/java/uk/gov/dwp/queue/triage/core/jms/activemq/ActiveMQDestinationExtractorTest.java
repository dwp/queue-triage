package uk.gov.dwp.queue.triage.core.jms.activemq;

import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.domain.Destination;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.domain.DestinationMatcher.aDestination;

public class ActiveMQDestinationExtractorTest {

    private final ActiveMQDestinationExtractor underTest = new ActiveMQDestinationExtractor("internal");

    @Test
    public void createWhenOriginalDestinationPresent() {
        ActiveMQMessage message = mock(ActiveMQMessage.class);
        when(message.getOriginalDestination()).thenReturn(new ActiveMQQueue("queue.name"));
        Destination destination = underTest.extractDestination(message);
        assertThat(destination, equalTo(new Destination("internal", Optional.of("queue.name"))));
    }

    @Test
    public void createWhenOriginalDestinationNotPresent() {
        ActiveMQMessage message = mock(ActiveMQMessage.class);
        when(message.getOriginalDestination()).thenReturn(null);
        Destination destination = underTest.extractDestination(message);
        assertThat(destination, is(aDestination().withBrokerName("internal").withNoName()));
    }
}