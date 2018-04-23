package uk.gov.dwp.queue.triage.web.server.api;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ofPattern;

public final class Constants {

    private static final DateTimeFormatter ISO_DATE_TIME_WITH_MS = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(UTC);

    private Constants() {
    }

    public static Optional<String> toIsoDateTimeWithMs(Instant instant) {
        return Optional.ofNullable(instant).map(ISO_DATE_TIME_WITH_MS::format);
    }

    public static Optional<Instant> toInstantFromIsoDateTime(String instantAsString) {
        return Optional.ofNullable(instantAsString).map(instant -> ISO_DATE_TIME_WITH_MS.parse(instant, Instant::from));
    }

}
