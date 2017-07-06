package uk.gov.dwp.queue.triage.core.jms.activemq;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQMessage;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.jms.DestinationExtractor;

import static java.util.Optional.ofNullable;

public class ActiveMQDestinationExtractor implements DestinationExtractor<ActiveMQMessage> {

    private final String brokerName;

    public ActiveMQDestinationExtractor(String brokerName) {
        this.brokerName = brokerName;
    }

    @Override
    public Destination extractDestination(ActiveMQMessage message) {
        return new Destination(
                brokerName,
                ofNullable(message.getOriginalDestination())
                        .map(ActiveMQDestination::getPhysicalName)
        );
    }
}