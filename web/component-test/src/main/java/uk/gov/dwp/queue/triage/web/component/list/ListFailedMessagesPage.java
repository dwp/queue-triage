package uk.gov.dwp.queue.triage.web.component.list;

import com.codeborne.selenide.Selenide;
import org.openqa.selenium.By;
import org.springframework.core.env.Environment;
import uk.gov.dwp.queue.triage.web.component.util.PageSupport;

import static com.codeborne.selenide.Condition.exist;

public class ListFailedMessagesPage {

    public static ListFailedMessagesPage openListFailedMessagePage(Environment environment) {
        return PageSupport.navigateTo(environment, ListFailedMessagesPage.class, "failed-messages");
    }

    public static ListFailedMessagesPage ensureLoaded() {
        Selenide.$(By.id("failed-message-list-grid")).should(exist);
        return new ListFailedMessagesPage();
    }
}
