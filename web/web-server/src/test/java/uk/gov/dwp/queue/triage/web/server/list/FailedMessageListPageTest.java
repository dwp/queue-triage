package uk.gov.dwp.queue.triage.web.server.list;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FailedMessageListPageTest {

    @Test
    public void createFailedMessageListPage() {
        FailedMessageListPage underTest = new FailedMessageListPage(true);

        assertTrue(underTest.isPopupRendered());
        assertEquals("list.mustache", underTest.getTemplate());
    }
}