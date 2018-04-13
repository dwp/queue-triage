package uk.gov.dwp.queue.triage.id;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class FailedMessageIdTest {

    private static final UUID A_UUID = UUID.randomUUID();
    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();

    @Test
    public void generateFailedMessageIdFromUUID() {
        assertEquals(A_UUID, FailedMessageId.fromUUID(A_UUID).getId());
    }

    @Test
    public void generateFailedMesasgeIdFromString() {
        assertEquals(A_UUID, FailedMessageId.fromString(A_UUID.toString()).getId());
    }

    @Test
    public void equalsTest() {
        assertFalse(FAILED_MESSAGE_ID.equals(null));
        assertFalse(FailedMessageId.fromUUID(A_UUID).equals(A_UUID));
        assertFalse(FailedMessageId.newFailedMessageId().equals(FAILED_MESSAGE_ID));
        assertTrue(FAILED_MESSAGE_ID.equals(FAILED_MESSAGE_ID));
        assertTrue(FailedMessageId.fromUUID(A_UUID).equals(FailedMessageId.fromUUID(A_UUID)));
    }

    @Test
    public void hashCodeTest() {
        assertNotEquals(FAILED_MESSAGE_ID.hashCode(), FailedMessageId.newFailedMessageId().hashCode());
        assertEquals(FAILED_MESSAGE_ID.getId().hashCode(), FAILED_MESSAGE_ID.hashCode());
    }

    @Test
    public void toStringTest() {
        assertEquals(FAILED_MESSAGE_ID.getId().toString(), FAILED_MESSAGE_ID.toString());
    }
}