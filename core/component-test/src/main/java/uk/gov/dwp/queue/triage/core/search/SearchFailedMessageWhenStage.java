package uk.gov.dwp.queue.triage.core.search;

import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.SearchFailedMessageRequestBuilder;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.client.search.SearchRequestBuilderArgumentFormatter;
import uk.gov.dwp.queue.triage.jgiven.WhenStage;

import java.util.Collection;

@JGivenStage
public class SearchFailedMessageWhenStage extends WhenStage<SearchFailedMessageWhenStage> {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @ProvidedScenarioState
    private ResponseEntity<Collection<SearchFailedMessageResponse>> searchResponse;

    public SearchFailedMessageWhenStage aSearchIsRequestedForFailedMessages(@Format(value = SearchRequestBuilderArgumentFormatter.class) SearchFailedMessageRequestBuilder requestBuilder) {
        searchResponse = testRestTemplate.exchange(
                "/core/failed-message/search",
                HttpMethod.POST,
                new HttpEntity<>(requestBuilder.build()),
                new ParameterizedTypeReference<Collection<SearchFailedMessageResponse>>() {
                });
        return this;
    }
}
