package uk.gov.dwp.queue.triage.web.server.search;

import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.web.server.list.FailedMessageListItem;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ofPattern;

public class FailedMessageListItemAdapter {

    private static final DateTimeFormatter ISO_DATE_TIME_WITH_MS = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(UTC);
    private static final Predicate<Set<String>> IS_EMPTY = Set::isEmpty;

    public List<FailedMessageListItem> adapt(Collection<SearchFailedMessageResponse> failedMessages) {
        return failedMessages
                .stream()
                .map(fm -> new FailedMessageListItem(
                        fm.getFailedMessageId().getId().toString(),
                        Optional.ofNullable(fm.getContent()).map(content -> content.replace("\"", "\\\"")).orElse(null),
                        fm.getBroker(),
                        fm.getDestination().orElse(null),
                        toString(fm.getSentDateTime()),
                        toString(fm.getLastFailedDateTime()),
                        Optional.ofNullable(fm.getLabels())
                                .filter(IS_EMPTY.negate())
                                .map(Collection::stream)
                                .map(x -> x.collect(Collectors.joining(", ")))
                                .orElse(null)))
                .collect(Collectors.toList());
    }

    private String toString(Instant instant) {
        return (instant != null) ? ISO_DATE_TIME_WITH_MS.format(instant) : null;
    }
}
