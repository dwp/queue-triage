package uk.gov.dwp.queue.triage.web.component.home;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

public class HomePage {

    public static HomePage ensureLoaded() {
        $(By.id("queue-triage-home")).should(Condition.exist);
        return new HomePage();
    }
}
