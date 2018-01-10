package uk.gov.dwp.queue.triage.core.domain;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.client.status.StatusHistoryResponse;

import java.time.Instant;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class StatusHistoryResponseMatcher extends TypeSafeDiagnosingMatcher<StatusHistoryResponse> {

    private Matcher<FailedMessageStatus> status = notNullValue(FailedMessageStatus.class);
    private Matcher<Instant> effectiveDateTime = notNullValue(Instant.class);

    public static StatusHistoryResponseMatcher statusHistoryResponse() {
        return new StatusHistoryResponseMatcher();
    }

    @Override
    protected boolean matchesSafely(StatusHistoryResponse statusHistoryResponse, Description description) {
        final FailedMessageStatus status = statusHistoryResponse.getStatus();
        final Instant effectiveDateTime = statusHistoryResponse.getEffectiveDateTime();
        description
                .appendText("status = ").appendValue(status)
                .appendText(", effectiveDateTime = ").appendValue(effectiveDateTime);
        return this.status.matches(status) &&
                this.effectiveDateTime.matches(effectiveDateTime);
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("status is ").appendValue(status)
                .appendText(", effectiveDateTime is ").appendValue(effectiveDateTime);
    }

    public StatusHistoryResponseMatcher withStatus(FailedMessageStatus status) {
        this.status = equalTo(status);
        return this;
    }

    public StatusHistoryResponseMatcher withEffectiveDateTime(Instant statusDateTime) {
        this.effectiveDateTime = equalTo(statusDateTime);
        return this;
    }
}
