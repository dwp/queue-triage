package uk.gov.dwp.queue.triage.web.server.list;

import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ofPattern;

public class FailedMessagesJsonSerializer {

    private static final DateTimeFormatter ISO_DATE_TIME_WITH_MS = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(UTC);

    public String asJson(Collection<SearchFailedMessageResponse> failedMessages) {
        return failedMessages
                .stream()
                .map(fm -> new StringBuilder().append("{ ")
                        .append("recid: \"").append(fm.getFailedMessageId().getId()).append("\", ")
                        .append("content: \"").append(Optional.ofNullable(fm.getContent()).map(content -> content.replace("\"", "\\\"")).orElse("")).append("\", ")
                        .append("broker: \"").append(fm.getBroker()).append("\", ")
                        .append("destination: \"").append(fm.getDestination().orElse("null")).append("\", ")
                        .append("sentDateTime: \"").append(asJson(fm.getSentDateTime())).append("\", ")
                        .append("failedDateTime: \"").append(asJson(fm.getLastFailedDateTime())).append("\", ")
                        .append("}"))
                .collect(Collectors.joining(",", "[", "]"));
    }

    private String asJson(Instant instant) {
        return (instant != null) ? ISO_DATE_TIME_WITH_MS.format(instant) : null;
    }
}
