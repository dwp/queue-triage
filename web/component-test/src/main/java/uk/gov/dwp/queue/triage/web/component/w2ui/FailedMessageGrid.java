package uk.gov.dwp.queue.triage.web.component.w2ui;

import com.codeborne.selenide.Selenide;
import org.openqa.selenium.By;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

public class FailedMessageGrid {

    public static void selectCheckboxForFailedMessage(FailedMessageId failedMessageId) {
        Selenide.$(By.id("grid_failedMessages_frec_" + failedMessageId))
                .click();
    }

}
