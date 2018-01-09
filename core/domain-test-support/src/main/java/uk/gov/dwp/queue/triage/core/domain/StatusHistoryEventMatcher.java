package uk.gov.dwp.queue.triage.core.domain;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status;

import java.time.Instant;

import static org.hamcrest.Matchers.notNullValue;

public class StatusHistoryEventMatcher extends TypeSafeMatcher<StatusHistoryEvent> {

    private Status status;
    private Matcher<Instant> updatedDateTimeMatcher = notNullValue(Instant.class);

    public static StatusHistoryEventMatcher equalTo(Status status) {
        return new StatusHistoryEventMatcher()
                .withStatus(status);
    }

    private StatusHistoryEventMatcher withStatus(Status status) {
        this.status = status;
        return this;
    }

    public StatusHistoryEventMatcher withUpdatedDateTime(Instant updatedDateTime) {
        this.updatedDateTimeMatcher = Matchers.equalTo(updatedDateTime);
        return this;
    }

    public StatusHistoryEventMatcher withUpdatedDateTime(Matcher<Instant> updatedDateTimeMatcher) {
        this.updatedDateTimeMatcher = updatedDateTimeMatcher;
        return this;
    }

    @Override
    protected boolean matchesSafely(StatusHistoryEvent statusHistoryEvent) {
        return status == statusHistoryEvent.getStatus() &&
                updatedDateTimeMatcher.matches(statusHistoryEvent.getEffectiveDateTime());
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("status is ").appendValue(status)
                .appendText(" updatedDateTime is ").appendDescriptionOf(updatedDateTimeMatcher);
    }
}
