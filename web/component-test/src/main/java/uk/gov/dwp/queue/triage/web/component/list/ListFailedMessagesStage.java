package uk.gov.dwp.queue.triage.web.component.list;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.mockito.Mockito;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import uk.gov.dwp.queue.triage.core.client.SearchFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse.SearchFailedMessageResponseBuilder;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

@JGivenStage
public class ListFailedMessagesStage extends Stage<ListFailedMessagesStage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListFailedMessagesStage.class);

    @Autowired
    private SearchFailedMessageClient searchFailedMessageClient;
    @Autowired
    private Environment environment;

    @ProvidedScenarioState
    private Map<FailedMessageId, SearchFailedMessageResponse> searchFailedMessageResponses = new HashMap<>();

    public ListFailedMessagesStage aFailedMessage$Exists(SearchFailedMessageResponseBuilder builder) {
        SearchFailedMessageResponse response = builder.build();
        searchFailedMessageResponses.put(response.getFailedMessageId(), response);
        return this;
    }

    public ListFailedMessagesStage theSearchResultsWillContainNoFailedMessages() {
        Mockito.when(searchFailedMessageClient.search(any(SearchFailedMessageRequest.class)))
                .thenReturn(Collections.emptyList());
        return this;
    }

    public ListFailedMessagesStage theSearchResultsWillContainFailedMessages$(FailedMessageId...failedMessageIds) {
        List<SearchFailedMessageResponse> searchResponses = Arrays
                .stream(failedMessageIds)
                .map(this.searchFailedMessageResponses::get)
                .collect(Collectors.toList());
        Mockito.when(searchFailedMessageClient.search(any(SearchFailedMessageRequest.class)))
                .thenReturn(searchResponses);
        return this;
    }

    public ListFailedMessagesStage theUserNavigatesToTheFailedMessagesPage() {
        LOGGER.debug("Opening the ListFailedMessagePage");
        ListFailedMessagesPage.openListFailedMessagePage(environment);
        return this;
    }

    public ListFailedMessagesStage theMessageListIsEmpty() {
        LOGGER.debug("Asserting the MessageList is empty");
        assertThat(Selenide.$$(By.cssSelector("tr[recid]")).size(), is(0));
        return this;
    }

    public ListFailedMessagesStage theMessageListContainsAFailedMessageWithId(FailedMessageId failedMessageId) {
        LOGGER.debug("Asserting the Messagelist contains: {}", failedMessageId);
//        assertThat(Selenide.$(By.cssSelector("tr[recid='" + failedMessageId.toString() + "']")).exists(), is(true));
        Selenide.$(By.cssSelector("tr[recid='" + failedMessageId.toString() + "']")).waitUntil(Condition.exist, 5000);
        return this;
    }

    public ListFailedMessagesStage theUserClicksTheReloadButton() {
        LOGGER.debug("User is about to click the Reload Button");
        Selenide.$(By.className("w2ui-icon-reload")).click();
        LOGGER.debug("User has clicked the Reload Button");
//        SelenideElement refreshingSpinner = Selenide.$(By.className("w2ui-lock-msg"));
//        int count = 0;
//        while (refreshingSpinner.isDisplayed() && count++ < 20) {
//            try {
//                Thread.sleep(100);
//                refreshingSpinner = Selenide.$(By.className("w2ui-lock-msg")).waitUntil(Condition.not(Condition.visible), 2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        Selenide.$(By.className("w2ui-lock-msg")).waitUntil(Condition.not(Condition.visible), 5000);
        LOGGER.debug("Spinner no longer visible");
        return this;
    }
}
