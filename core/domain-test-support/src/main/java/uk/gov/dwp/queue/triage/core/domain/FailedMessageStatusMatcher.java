package uk.gov.dwp.queue.triage.core.domain;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status;

import java.time.Instant;

import static org.hamcrest.Matchers.notNullValue;

public class FailedMessageStatusMatcher extends TypeSafeMatcher<FailedMessageStatus> {

    private Status status;
    private Matcher<Instant> updatedDateTimeMatcher = notNullValue(Instant.class);

    public static FailedMessageStatusMatcher equalTo(Status status) {
        return new FailedMessageStatusMatcher()
                .withStatus(status);
    }

    private FailedMessageStatusMatcher withStatus(Status status) {
        this.status = status;
        return this;
    }

    public FailedMessageStatusMatcher withUpdatedDateTime(Instant updatedDateTime) {
        this.updatedDateTimeMatcher = Matchers.equalTo(updatedDateTime);
        return this;
    }

    public FailedMessageStatusMatcher withUpdatedDateTime(Matcher<Instant> updatedDateTimeMatcher) {
        this.updatedDateTimeMatcher = updatedDateTimeMatcher;
        return this;
    }

    @Override
    protected boolean matchesSafely(FailedMessageStatus failedMessageStatus) {
        return status == failedMessageStatus.getStatus() &&
                updatedDateTimeMatcher.matches(failedMessageStatus.getEffectiveDateTime());
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("status is").appendValue(status)
                .appendText("updatedDateTime is").appendDescriptionOf(updatedDateTimeMatcher);
    }
}
