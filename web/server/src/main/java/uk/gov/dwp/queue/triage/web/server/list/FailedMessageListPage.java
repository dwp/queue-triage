package uk.gov.dwp.queue.triage.web.server.list;

import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.web.common.Page;

import java.util.Collection;

public class FailedMessageListPage extends Page {

    private final Collection<SearchFailedMessageResponse> failedMessages;
    private final FailedMessagesJsonSerializer failedMessagesJsonSerializer;

    public FailedMessageListPage(Collection<SearchFailedMessageResponse> failedMessages,
                                 FailedMessagesJsonSerializer failedMessagesJsonSerializer) {
        super("list.mustache");
        this.failedMessages = failedMessages;
        this.failedMessagesJsonSerializer = failedMessagesJsonSerializer;
    }

    public Collection<SearchFailedMessageResponse> getFailedMessages() {
        return failedMessages;
    }

    public String getFailedMessagesAsJson() {
        return failedMessagesJsonSerializer.asJson(failedMessages);
    }
}
