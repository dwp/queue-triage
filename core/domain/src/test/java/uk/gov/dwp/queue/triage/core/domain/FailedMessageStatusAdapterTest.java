package uk.gov.dwp.queue.triage.core.domain;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatusAdapter.fromFailedMessageStatus;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatusAdapter.toFailedMessageStatus;

public class FailedMessageStatusAdapterTest {

    @Test
    public void ensureStatusCanBeAdaptedFromInternalToExternal() throws Exception {
        List<Status> internalStatuses = Arrays.asList(Status.DELETED);

        for (Status status : Status.values()) {
            try {
                assertThat(toFailedMessageStatus(status), is(notNullValue()));
            } catch (IllegalArgumentException e) {
                if (!internalStatuses.contains(status)) {
                    fail(format("Status '%s' has no mapping to %s. Should this status be visible in the public API?",
                            status,
                            FailedMessageStatus.class));
                }
            }
        }
    }

    @Test
    public void ensureStatusCanBeAdaptedFromExternalToInternal() {
        for (FailedMessageStatus failedMessageStatus : FailedMessageStatus.values()) {
            try {
                assertThat(fromFailedMessageStatus(failedMessageStatus), is(notNullValue()));
            } catch (IllegalArgumentException e) {
                fail(format("FailedMessageStatus '%s' has no mapping to %s", failedMessageStatus, Status.class));
            }
        }
    }

    @Test
    public void setOfFailedMessageStatusAreAdaptedCorrectly() {
        assertThat(
                fromFailedMessageStatus(newHashSet(FailedMessageStatus.values())),
                containsInAnyOrder(Status.FAILED, Status.CLASSIFIED, Status.RESEND, Status.SENT)
        );
    }
}