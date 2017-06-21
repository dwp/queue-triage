package uk.gov.dwp.queue.triage.id;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.UUID;

public class FailedMessageId implements Id {

    public static final String FAILED_MESSAGE_ID = "failedMessageId";
    private final UUID id;

    private FailedMessageId(UUID id) {
        this.id = id;
    }

    public static FailedMessageId newFailedMessageId() {
        return new FailedMessageId(UUID.randomUUID());
    }

    public static FailedMessageId fromUUID(UUID uuid) {
        return new FailedMessageId(uuid);
    }

    @JsonCreator
    public static FailedMessageId fromString(String uuid) {
        return new FailedMessageId(UUID.fromString(uuid));
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return id.equals(((FailedMessageId) o).id);
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
