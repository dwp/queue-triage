package uk.gov.dwp.queue.triage.web.component.search;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.jgiven.WhenStage;

@JGivenStage
public class SearchFailedMessageWhenStage extends WhenStage<SearchFailedMessageWhenStage> {

    private static Logger LOGGER = LoggerFactory.getLogger(SearchFailedMessageWhenStage.class);

    public SearchFailedMessageWhenStage theUserSearchesFor(String searchText) {
        LOGGER.debug("Searching for failedMessages containing any field equal to '{}'", searchText);
        Selenide.$(By.id("grid_failedMessages_search_all"))
                .sendKeys(searchText + Keys.RETURN);
        return this;
    }

    public SearchFailedMessageWhenStage theUserSearchesForTheField$EqualTo$(String fieldName, String searchText) {
        LOGGER.debug("Searching for failedMessages with '{}' equal to '{}'", fieldName, searchText);
        Selenide.$(By.id("tb_failedMessages_toolbar_item_w2ui-search"))
                .find(By.className("icon-search-down"))
                .click();
        Selenide.$(By.id("w2ui-overlay-failedMessages-searchFields"))
                .findAll(By.tagName("td"))
                .findBy(Condition.text(fieldName))
                .click();
        Selenide.$(By.id("grid_failedMessages_search_all"))
                .sendKeys(searchText + Keys.RETURN);
        return this;
    }
}
