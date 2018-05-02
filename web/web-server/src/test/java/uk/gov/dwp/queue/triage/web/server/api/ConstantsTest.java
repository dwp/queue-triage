package uk.gov.dwp.queue.triage.web.server.api;

import org.junit.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ConstantsTest {

    @Test
    public void formatNullInstant() {
        assertEquals(Optional.empty(), Constants.toIsoDateTimeWithMs(null));
    }

    @Test
    public void formatGivenInstant() {
        assertEquals(Optional.of("1970-01-01T00:00:00.000Z"), Constants.toIsoDateTimeWithMs(Instant.ofEpochMilli(0)));
    }
}