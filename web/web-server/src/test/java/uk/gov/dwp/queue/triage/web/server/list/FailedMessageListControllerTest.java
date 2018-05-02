package uk.gov.dwp.queue.triage.web.server.list;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class FailedMessageListControllerTest {

    private final FailedMessageListController underTest = new FailedMessageListController(true);

    @Test
    public void getFailedMessageListPage() {
        final FailedMessageListPage failedMessages = underTest.getFailedMessages();

        assertThat(failedMessages.getTemplate(), is(equalTo("list.mustache")));
        assertThat(failedMessages.isPopupRendered(), is(true));
    }
}