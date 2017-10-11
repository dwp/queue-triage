package uk.gov.dwp.queue.triage.web.component.resend;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.WhenStage;

@JGivenStage
public class ResendMessageWhenStage extends WhenStage<ResendMessageWhenStage> {

    private static Logger LOGGER = LoggerFactory.getLogger(ResendMessageWhenStage.class);

    public ResendMessageWhenStage failedMessage$IsSelected(FailedMessageId failedMessageId) {
        LOGGER.debug("Selecting failedMessage: {} for resending", failedMessageId);
        Selenide.$(By.id("grid_failedMessages_frec_" + failedMessageId))
                .click();
        return this;
    }

    public ResendMessageWhenStage theUserClicksResend() {
        LOGGER.debug("Resending failedMessages");
        Selenide.$(By.id("tb_failedMessages_toolbar_item_w2ui-resend"))
                .find(".w2ui-button")
                .click();
        return this;
    }

    public ResendMessageWhenStage theUserConfirmsTheyWantToResendTheMessages() {
        Selenide.$(By.id("w2ui-message0"))
                .find(".w2ui-btn")
                .click();
        return this;
    }
}
