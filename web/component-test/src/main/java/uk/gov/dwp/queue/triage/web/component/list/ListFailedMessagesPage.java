package uk.gov.dwp.queue.triage.web.component.list;

import org.springframework.core.env.Environment;
import uk.gov.dwp.queue.triage.web.component.util.PageSupport;

public class ListFailedMessagesPage {

    public static ListFailedMessagesPage openListFailedMessagePage(Environment environment) {
        return PageSupport.navigateTo(environment, ListFailedMessagesPage.class, "failed-messages");
    }
}
