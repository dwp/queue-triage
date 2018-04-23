package uk.gov.dwp.queue.triage.web.server.api.status;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.client.status.FailedMessageStatusHistoryClient;
import uk.gov.dwp.queue.triage.core.client.status.StatusHistoryResponse;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.web.server.w2ui.W2UIResponse;

import java.time.Instant;
import java.util.Collections;

import static org.mockito.Mockito.when;

public class StatusHistoryResourceTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();
    private static final Instant NOW = Instant.now();

    @Rule
    public MockitoRule mockitoJUnit = MockitoJUnit.rule();
    @Mock
    private FailedMessageStatusHistoryClient failedMessageStatusHistoryClient;
    @InjectMocks
    private StatusHistoryResource underTest;

    @Test
    public void adaptStatusHistoryResponseToStatusHistoryListItem() {
        when(failedMessageStatusHistoryClient.getStatusHistory(FAILED_MESSAGE_ID)).thenReturn(Collections.singletonList(new StatusHistoryResponse(FailedMessageStatus.FAILED, NOW)));

//        assertThat(underTest.statusHistory(FAILED_MESSAGE_ID), contains(statusHistoryListItem(equalTo())));
    }

    private Matcher<W2UIResponse<StatusHistoryListItem>> success(int totalResults, Matcher<Iterable<? extends StatusHistoryListItem>> results) {
        return new TypeSafeMatcher<W2UIResponse<StatusHistoryListItem>>() {
            @Override
            protected boolean matchesSafely(W2UIResponse<StatusHistoryListItem> item) {
                return "success".equals(item.getStatus());
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

    private Matcher<StatusHistoryListItem> statusHistoryListItem(Matcher<String> recId, Matcher<String> status, Matcher<String> effectiveDateTime) {
        return new TypeSafeMatcher<StatusHistoryListItem>() {
            @Override
            protected boolean matchesSafely(StatusHistoryListItem item) {
                return recId.matches(item.getRecId())
                        && status.matches(item.getStatus())
                        && effectiveDateTime.matches(item.getEffectiveDateTime());
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}