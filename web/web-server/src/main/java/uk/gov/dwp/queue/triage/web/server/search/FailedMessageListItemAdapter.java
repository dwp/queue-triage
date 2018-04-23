package uk.gov.dwp.queue.triage.web.server.search;

import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.web.server.list.FailedMessageListItem;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static uk.gov.dwp.queue.triage.web.server.api.Constants.toIsoDateTimeWithMs;

public class FailedMessageListItemAdapter {

    private static final Predicate<Set<String>> IS_EMPTY = Set::isEmpty;

    public List<FailedMessageListItem> adapt(Collection<SearchFailedMessageResponse> failedMessages) {
        return failedMessages
                .stream()
                .map(fm -> new FailedMessageListItem(
                        fm.getFailedMessageId().getId().toString(),
                        Optional.ofNullable(fm.getContent()).map(content -> content.replace("\"", "\\\"")).orElse(null),
                        fm.getBroker(),
                        fm.getDestination().orElse(null),
                        toIsoDateTimeWithMs(fm.getSentDateTime()).orElse(null),
                        toIsoDateTimeWithMs(fm.getLastFailedDateTime()).orElse(null),
                        Optional.ofNullable(fm.getLabels())
                                .filter(IS_EMPTY.negate())
                                .map(x -> String.join(", ", x))
                                .orElse(null)))
                .collect(Collectors.toList());
    }
}
