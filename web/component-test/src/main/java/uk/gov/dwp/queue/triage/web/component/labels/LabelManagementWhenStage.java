package uk.gov.dwp.queue.triage.web.component.labels;

import com.codeborne.selenide.Selenide;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.WhenStage;
import uk.gov.dwp.queue.triage.web.component.w2ui.FailedMessageGrid;

import static uk.gov.dwp.queue.triage.web.component.w2ui.FailedMessageGrid.FailedMessageGridColumn.LABELS_COLUMN;

public class LabelManagementWhenStage extends WhenStage<LabelManagementWhenStage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabelManagementWhenStage.class);

    public LabelManagementWhenStage theUserAddslabels$ToFailedMessage$(String labels, FailedMessageId failedMessageId) {
        LOGGER.debug("Adding label(s): {} to failedMessage: {}", labels, failedMessageId);
        FailedMessageGrid.selectCheckboxForFailedMessage(failedMessageId);

        FailedMessageGrid.setValueForCell(failedMessageId, LABELS_COLUMN, labels);

        FailedMessageGrid.selectCheckboxForFailedMessage(failedMessageId);
        return this;
    }

    public LabelManagementWhenStage theUserClicksSave() {
        LOGGER.debug("Saving changes to the failedMessages grid");
        Selenide.$(By.id("tb_failedMessages_toolbar_item_w2ui-save"))
                .find(".w2ui-button")
                .click();
        return this;
    }
}
