package uk.gov.dwp.queue.triage.core.domain;

import org.junit.Test;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FailedMessageNotFoundExceptionTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();

    @Test
    public void verifyMessage() {
        final FailedMessageNotFoundException failedMessageNotFoundException = new FailedMessageNotFoundException(FAILED_MESSAGE_ID);

        assertEquals("Failed Message: " + FAILED_MESSAGE_ID + " not found", failedMessageNotFoundException.getMessage());
        assertEquals("Failed Message: " + FAILED_MESSAGE_ID + " not found", failedMessageNotFoundException.getLocalizedMessage());
        assertNull(failedMessageNotFoundException.getCause());
    }
}