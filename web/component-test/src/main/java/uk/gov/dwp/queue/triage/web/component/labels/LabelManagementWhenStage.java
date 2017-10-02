package uk.gov.dwp.queue.triage.web.component.labels;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.WhenStage;

public class LabelManagementWhenStage extends WhenStage<LabelManagementWhenStage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabelManagementWhenStage.class);

    public LabelManagementWhenStage theUserAddslabels$ToFailedMessage$(String labels, FailedMessageId failedMessageId) {
        LOGGER.debug("Adding label(s): {} to failedMessage: {}", labels, failedMessageId);
        selectCheckboxForFailedMessage(failedMessageId, true);

        SelenideElement cell = Selenide.$("#grid_failedMessages_rec_" + failedMessageId + " td[col='5'] div");

        // Double clicking in w2ui is actually two single-clicks < 350ms apart
        cell.click();
        Selenide.sleep(5);
        cell.click();

        Selenide.$("#grid_failedMessages_edit_" + failedMessageId + "_5").setValue(labels);

        selectCheckboxForFailedMessage(failedMessageId, false);
        return this;
    }

    private void selectCheckboxForFailedMessage(FailedMessageId failedMessageId, boolean selected) {
        Selenide.$(By.id("grid_failedMessages_frec_" + failedMessageId))
                .find("input[type='checkbox']")
                .setSelected(selected);
    }

    public LabelManagementWhenStage theUserClicksSave() {
        LOGGER.debug("Saving changes to the failedMessages grid");
        Selenide.$(By.id("tb_failedMessages_toolbar_item_w2ui-save"))
                .find(".w2ui-button")
                .click();
        return this;
    }
}
