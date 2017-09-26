package uk.gov.dwp.queue.triage.web.component.labels;

import com.codeborne.selenide.Selenide;
import com.tngtech.jgiven.annotation.Description;
import org.openqa.selenium.By;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.WhenStage;

public class LabelManagementWhenStage extends WhenStage<LabelManagementWhenStage> {


    @Description("the user adds label(s) '$' to failed message $")
    public LabelManagementWhenStage labelsAddedToFailedMessage(String label, FailedMessageId failedMessageId) {
        // Select the row
        Selenide.$(By.cssSelector("tr[recid='" + failedMessageId + "']")).click();
        // Double click on the row
        Selenide.$("#grid_failed-message-list-grid_rec_" + failedMessageId).doubleClick();
        // Set the labels
        Selenide.$("input[recid='" + failedMessageId + "']").setValue(label);
        return this;
    }

    public LabelManagementWhenStage theUserClicksSave() {
        Selenide.$(By.id("tb_failed-message-list-grid_toolbar_item_w2ui-save")).click();
        return this;
    }
}
