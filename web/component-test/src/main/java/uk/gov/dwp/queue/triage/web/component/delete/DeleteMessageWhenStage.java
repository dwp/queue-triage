package uk.gov.dwp.queue.triage.web.component.delete;

import com.codeborne.selenide.Selenide;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.WhenStage;

@JGivenStage
public class DeleteMessageWhenStage extends WhenStage<DeleteMessageWhenStage> {

    private static Logger LOGGER = LoggerFactory.getLogger(DeleteMessageWhenStage.class);

    public DeleteMessageWhenStage failedMessage$IsSelected(FailedMessageId failedMessageId) {
        LOGGER.debug("Selecting failedMessage: {} for deletion", failedMessageId);
        Selenide.$(By.id("grid_failedMessages_frec_" + failedMessageId))
                .click();
        return this;
    }

    public DeleteMessageWhenStage theUserClicksDelete() {
        LOGGER.debug("Deleting failedMessages");
        Selenide.$(By.id("tb_failedMessages_toolbar_item_w2ui-delete"))
                .find(".w2ui-button")
                .click();
        return this;
    }

    public DeleteMessageWhenStage theUserConfirmsTheyWantToDeleteTheMessages() {
        Selenide.$(By.id("w2ui-message0"))
                .find(".w2ui-btn")
                .click();
        return this;
    }
}
