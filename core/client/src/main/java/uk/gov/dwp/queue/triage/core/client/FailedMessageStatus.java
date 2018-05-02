package uk.gov.dwp.queue.triage.core.client;

public enum FailedMessageStatus {
    FAILED("Failed"),
    RESENDING("Resending"),
    SENT("Sent");

    private final String description;

    FailedMessageStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
