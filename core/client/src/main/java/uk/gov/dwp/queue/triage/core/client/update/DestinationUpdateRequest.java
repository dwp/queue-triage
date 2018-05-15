package uk.gov.dwp.queue.triage.core.client.update;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DestinationUpdateRequest implements UpdateRequest {

    private final String broker;
    private final String destination;

    public DestinationUpdateRequest(@JsonProperty("broker") String broker,
                                    @JsonProperty("destination") String destination) {
        this.broker = broker;
        this.destination = destination;
    }

    public String getBroker() {
        return broker;
    }

    public String getDestination() {
        return destination;
    }
}
