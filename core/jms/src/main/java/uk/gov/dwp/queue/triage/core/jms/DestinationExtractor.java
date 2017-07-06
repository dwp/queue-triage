package uk.gov.dwp.queue.triage.core.jms;

import uk.gov.dwp.queue.triage.core.domain.Destination;

import javax.jms.Message;

public interface DestinationExtractor<T extends Message>  {

    Destination extractDestination(T message);
}
