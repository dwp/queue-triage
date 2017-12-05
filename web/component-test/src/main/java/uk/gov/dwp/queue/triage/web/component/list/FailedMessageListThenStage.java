package uk.gov.dwp.queue.triage.web.component.list;

import com.codeborne.selenide.Selenide;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.ThenStage;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exist;

public class FailedMessageListThenStage extends ThenStage<FailedMessageListThenStage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FailedMessageListThenStage.class);

    public FailedMessageListThenStage theMessageListIsEmpty() {
        LOGGER.debug("Asserting the MessageList is empty");
        Selenide.$$(By.cssSelector("tr[recid]")).shouldHave(size(0));
        return this;
    }

    public FailedMessageListThenStage theMessageListContainsAFailedMessageWithId(FailedMessageId failedMessageId) throws InterruptedException {
        LOGGER.debug("Asserting FailedMessageId: {} is in the MessageList", failedMessageId);
        Selenide.$(By.cssSelector("tr[recid='" + failedMessageId.toString() + "']")).should(exist);
        return this;
    }

}
