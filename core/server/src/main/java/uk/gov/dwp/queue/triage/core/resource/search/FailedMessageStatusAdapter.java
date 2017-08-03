package uk.gov.dwp.queue.triage.core.resource.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status;

public class FailedMessageStatusAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedMessageStatusAdapter.class);

    public FailedMessageStatus toFailedMessageStatus(Status status) {
        switch (status) {
            case FAILED:
                return uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.FAILED;
            case RESEND:
                return uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.RESENDING;
            case SENT:
                return uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.SENT;
            default:
                LOGGER.error("Status '{}' has no mapping to {}.  Should a message in this status be visible in the public API?",
                        status, FailedMessageStatus.class);
                throw new IllegalArgumentException("Internal Status cannot be mapped");
        }
    }
}
