package uk.gov.dwp.queue.triage.web.server.search;

import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.web.server.list.FailedMessageListItem;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FailedMessageListItemAdapter {

    public List<FailedMessageListItem> adapt(Collection<SearchFailedMessageResponse> failedMessages) {
        return failedMessages
                .stream()
                .map(FailedMessageListItem::new)
                .collect(Collectors.toList());
    }
}
