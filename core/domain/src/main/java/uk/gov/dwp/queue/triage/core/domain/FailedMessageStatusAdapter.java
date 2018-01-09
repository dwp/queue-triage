package uk.gov.dwp.queue.triage.core.domain;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FailedMessageStatusAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedMessageStatusAdapter.class);

    private static Map<Status, FailedMessageStatus> statusToFailedMessageStatus = new HashMap<Status, FailedMessageStatus>() {{
        put(Status.FAILED, FailedMessageStatus.FAILED);
        put(Status.CLASSIFIED, FailedMessageStatus.FAILED);
        put(Status.RESEND, FailedMessageStatus.RESENDING);
        put(Status.SENT, FailedMessageStatus.SENT);
    }};

    private static Map<FailedMessageStatus, Set<Status>> failedMessageStatusToStatusMapping = new HashMap<FailedMessageStatus, Set<Status>>() {{
        put(FailedMessageStatus.FAILED, Sets.immutableEnumSet(Status.FAILED, Status.CLASSIFIED));
        put(FailedMessageStatus.RESENDING, Sets.immutableEnumSet(Status.RESEND));
        put(FailedMessageStatus.SENT, Sets.immutableEnumSet(Status.SENT));
    }};


    public static FailedMessageStatus toFailedMessageStatus(Status status) {
        FailedMessageStatus failedMessageStatus = statusToFailedMessageStatus.get(status);
        if (failedMessageStatus == null) {
            LOGGER.error(
                    "Status '{}' has no mapping to {}.  Should a message in this status be visible in the public API?",
                    status, FailedMessageStatus.class
            );
            throw new IllegalArgumentException("Internal Status cannot be mapped");
        }
        return failedMessageStatus;
    }

    public static Set<Status> fromFailedMessageStatus(Set<FailedMessageStatus> statuses) {
        Set<Status> output = new HashSet<>();
        for (FailedMessageStatus failedMessageStatus : statuses) {
            output.addAll(fromFailedMessageStatus(failedMessageStatus));
        }
        return output;
    }

    public static Set<Status> fromFailedMessageStatus(FailedMessageStatus status) {
        return failedMessageStatusToStatusMapping.get(status);
    }
}
