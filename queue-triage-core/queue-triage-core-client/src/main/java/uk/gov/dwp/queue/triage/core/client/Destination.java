package uk.gov.dwp.queue.triage.core.client;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Destination {

    public static final String BROKER_NAME = "brokerName";
    public static final String NAME = "name";

    private final String brokerName;
    private final String name;

    public Destination(@JsonProperty(BROKER_NAME) String brokerName,
                       @JsonProperty(NAME) String name) {
        this.brokerName = brokerName;
        this.name = name;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Destination that = (Destination) o;

        return brokerName.equals(that.brokerName)
                && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return 31 * brokerName.hashCode() + name.hashCode();
    }

    @Override
    public String toString() {
        return "Destination{" +
                "brokerName='" + brokerName + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
