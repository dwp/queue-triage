package uk.gov.dwp.queue.triage.core.domain.update;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status;

import java.time.Instant;

import static org.hamcrest.Matchers.notNullValue;

public class StatusUpdateRequestMatcher extends TypeSafeMatcher<StatusUpdateRequest> {

    private Status status;
    private Matcher<Instant> updatedDateTimeMatcher = notNullValue(Instant.class);

    public static StatusUpdateRequestMatcher aStatusUpdateRequest(Status status) {
        return new StatusUpdateRequestMatcher()
                .withStatus(status);
    }

    private StatusUpdateRequestMatcher withStatus(Status status) {
        this.status = status;
        return this;
    }

    public StatusUpdateRequestMatcher withUpdatedDateTime(Instant updatedDateTime) {
        this.updatedDateTimeMatcher = Matchers.equalTo(updatedDateTime);
        return this;
    }

    public StatusUpdateRequestMatcher withUpdatedDateTime(Matcher<Instant> updatedDateTimeMatcher) {
        this.updatedDateTimeMatcher = updatedDateTimeMatcher;
        return this;
    }

    @Override
    protected boolean matchesSafely(StatusUpdateRequest statusUpdateRequest) {
        return status == statusUpdateRequest.getStatus() &&
                updatedDateTimeMatcher.matches(statusUpdateRequest.getEffectiveDateTime());
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("status is ").appendValue(status)
                .appendText(" updatedDateTime is ").appendDescriptionOf(updatedDateTimeMatcher);
    }
}
