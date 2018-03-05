package uk.gov.dwp.queue.triage.web.component.list;

import com.codeborne.selenide.Selenide;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import uk.gov.dwp.queue.triage.core.client.SearchFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse.SearchFailedMessageResponseBuilder;
import uk.gov.dwp.queue.triage.core.domain.SearchFailedMessageRequestMatcher;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.ReflectionArgumentFormatter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.visible;
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

    public ListFailedMessagesStage aFailedMessage$Exists(@Format(value = ReflectionArgumentFormatter.class) SearchFailedMessageResponseBuilder builder) {
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

    public ListFailedMessagesStage queueTriageCoreWillRespondTo(SearchFailedMessageRequestMatcher searchFailedMessageRequestMatcher,
                                                                SearchFailedMessageResponse... failedMessages) {
        Mockito.when(searchFailedMessageClient.search(MockitoHamcrest.argThat(searchFailedMessageRequestMatcher)))
                .thenReturn(Arrays.asList(failedMessages));
        return this;
    }

    public ListFailedMessagesStage theUserHasNavigatedToTheFailedMessagesPage() {
        return theUserNavigatesToTheFailedMessagesPage();
    }

    public ListFailedMessagesStage theUserNavigatesToTheFailedMessagesPage() {
        LOGGER.debug("Opening the ListFailedMessagePage");
        ListFailedMessagesPage.openListFailedMessagePage(environment);
        return this;
    }

    public ListFailedMessagesStage theUserClicksTheReloadButton() {
        LOGGER.debug("Reloading the failedMessages grid");
        // TODO: Investigate why this doesn't work with htmlunit-driver
        Selenide.$(By.id("tb_failedMessages_toolbar_item_w2ui-reload"))
                .find(By.className("w2ui-button"))
                .click();
        Selenide.$(By.className("w2ui-lock-msg")).waitUntil(not(visible), 5000);
        return this;
    }
}
