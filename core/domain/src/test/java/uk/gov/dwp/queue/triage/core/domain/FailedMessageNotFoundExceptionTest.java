package uk.gov.dwp.queue.triage.core.domain;

import org.junit.Test;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FailedMessageNotFoundExceptionTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();

    @Test
    public void verifyMessage() {
        final FailedMessageNotFoundException underTest = new FailedMessageNotFoundException(FAILED_MESSAGE_ID);

        assertEquals("Failed Message: " + FAILED_MESSAGE_ID + " not found", underTest.getMessage());
        assertEquals("Failed Message: " + FAILED_MESSAGE_ID + " not found", underTest.getLocalizedMessage());
        assertNull(underTest.getCause());
    }

    @Test
    public void verifySupplier() {
        final FailedMessageNotFoundException underTest = FailedMessageNotFoundException.failedMessageNotFound(FAILED_MESSAGE_ID).get();

        assertEquals("Failed Message: " + FAILED_MESSAGE_ID + " not found", underTest.getMessage());
        assertEquals("Failed Message: " + FAILED_MESSAGE_ID + " not found", underTest.getLocalizedMessage());
        assertNull(underTest.getCause());
    }
}