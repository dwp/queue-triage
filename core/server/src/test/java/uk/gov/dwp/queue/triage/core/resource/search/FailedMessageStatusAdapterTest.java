package uk.gov.dwp.queue.triage.core.resource.search;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class FailedMessageStatusAdapterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final FailedMessageStatusAdapter underTest = new FailedMessageStatusAdapter();

    @Test
    public void ensureStatusCanBeAdaptedFromInternalToExternal() throws Exception {
        List<Status> internalStatuses = Arrays.asList(Status.DELETED);

        for (Status status : Status.values()) {
            try {
                assertThat(underTest.toFailedMessageStatus(status), is(notNullValue()));
            } catch (IllegalArgumentException e) {
                if (!internalStatuses.contains(status)) {
                    fail(format("Status '%s' has no mapping to %s. Should this status be visible in the public API?",
                            status,
                            FailedMessageStatus.class));
                }
            }
        }
    }
}