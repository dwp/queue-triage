package uk.gov.dwp.queue.triage.web.component.w2ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

public class FailedMessageGrid {

    public static void selectCheckboxForFailedMessage(FailedMessageId failedMessageId) {
        Selenide.$(By.id("grid_failedMessages_frec_" + failedMessageId))
                .click();
    }

    public static SelenideElement selectRowInGrid(FailedMessageId failedMessageId) {
        return Selenide.$(By.id("grid_failedMessages_rec_" + failedMessageId));
    }


    public static SelenideElement selectCellForFailedMessage(FailedMessageId failedMessageId, FailedMessageGridColumn column) {
        return Selenide.$(By.id("grid_failedMessages_data_"
                        + selectRowInGrid(failedMessageId).getAttribute("index")
                        + "_" + column.getIndex()));
    }

    public static SelenideElement selectCellForFailedMessage(int row, int columnm) {
        return Selenide.$(By.id("grid_failedMessages_data_" + row + "_" + columnm));
    }

    // Double clicking in w2ui is actually two single-clicks < 350ms apart
    public static void doubleClick(WebElement webElement) {
        webElement.click();
        Selenide.sleep(5);
        webElement.click();

    }

    public static void setValueForCell(FailedMessageId failedMessageId, FailedMessageGridColumn column, String labels) {
        // Double clicking the cell should create a "grid_failedMessage_edit_${failedMessageId}_index" however we have
        // seen this fail in some older browsers
        doubleClick(selectCellForFailedMessage(failedMessageId, column));
        final SelenideElement labelCell = getEditCell(failedMessageId, column).waitUntil(Condition.exist, 100);
        if (labelCell.exists()) {
            labelCell.setValue(labels);
        } else {
            // If the "grid_failedMessage_edit_${failedMessageId}_index" does not exist - simulate what w2ui does.
            Selenide.executeJavaScript("w2ui.failedMessages.editField('" + failedMessageId + "'," + column.getIndex() + ");");
            getEditCell(failedMessageId, column).setValue(labels);
        }

    }

    private static SelenideElement getEditCell(FailedMessageId failedMessageId, FailedMessageGridColumn column) {
        return Selenide.$(By.id("grid_failedMessages_edit_" + failedMessageId + "_" + column.getIndex()));
    }

    public enum FailedMessageGridColumn {
        FAILED_MESSAGE_ID_COLUMN(0),
        BROKER_COLUMN(1),
        DESTINATION_COLUMN(2),
        DATE_SENT_COLUMN(3),
        DATE_FAILED_COLUMN(4),
        LABELS_COLUMN(5),
        CONTENT_COLUMN(6);

        private final int index;


        FailedMessageGridColumn(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }
}
