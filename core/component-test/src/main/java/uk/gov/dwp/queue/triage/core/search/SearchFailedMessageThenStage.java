package uk.gov.dwp.queue.triage.core.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsEmptyIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.SearchFailedMessageRequestBuilder;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponseMatcher;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseMatcher;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.ReflectionArgumentFormatter;

import javax.ws.rs.core.Response.Status;
import java.util.Collection;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;

@JGivenStage
public class SearchFailedMessageThenStage extends Stage<SearchFailedMessageThenStage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchFailedMessageThenStage.class);

    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @ExpectedScenarioState
    private ResponseEntity<Collection<SearchFailedMessageResponse>> searchResponse;

    public SearchFailedMessageThenStage a$HttpResponseCodeIsReceived(Status statusCode) {
        assertThat(searchResponse.getStatusCodeValue(), is(statusCode.getStatusCode()));
        return this;
    }

    public SearchFailedMessageThenStage aSearch$WillContain$(
            @Format(value = ReflectionArgumentFormatter.class, args = {"broker", "destination"}) SearchFailedMessageRequestBuilder requestBuilder,
            Matcher<Iterable<? extends SearchFailedMessageResponse>> resultsMatcher
    ) {
        await().pollInterval(100, MILLISECONDS)
                .until(() -> doSearch(requestBuilder), resultsMatcher);
        return this;
    }

    public SearchFailedMessageThenStage aSearch$ContainsNoResults(
            @Format(value = ReflectionArgumentFormatter.class, args = {"broker", "destination"}) SearchFailedMessageRequestBuilder requestBuilder
    ) {
        await().pollInterval(100, MILLISECONDS)
                .until(() -> doSearch(requestBuilder), emptyIterable());
        return this;
    }

    public SearchFailedMessageThenStage aSearch$WillContainAResponseWhere$(
            @Format(value = ReflectionArgumentFormatter.class, args = {"broker", "destination"}) SearchFailedMessageRequestBuilder requestBuilder,
            SearchFailedMessageResponseMatcher responseMatcher
    ) {
        await().pollInterval(100, MILLISECONDS)
                .until(() -> doSearch(requestBuilder), contains(responseMatcher));
        return this;
    }

    public SearchFailedMessageThenStage aFailedMessageWithId$Has(FailedMessageId failedMessageId,
                                                                 FailedMessageResponseMatcher failedMessageResponseMatcher) {
        await().pollInterval(500, MILLISECONDS).until(() -> findFailedMessageById(failedMessageId), failedMessageResponseMatcher);
        return this;
    }

    public SearchFailedMessageThenStage aFailedMessageWithId$DoesNotExist(FailedMessageId failedMessageId) {
        await().pollInterval(100, MILLISECONDS).until(() -> findFailedMessageById(failedMessageId), Matchers.nullValue());
        return this;
    }


    private Collection<SearchFailedMessageResponse> doSearch(SearchFailedMessageRequestBuilder requestBuilder) throws JsonProcessingException {
        SearchFailedMessageRequest searchRequest = requestBuilder.build();
        LOGGER.info("Executing Search: {}", objectMapper.writeValueAsString(searchRequest));
        return testRestTemplate.exchange(
                "/core/failed-message/search",
                HttpMethod.POST,
                new HttpEntity<>(searchRequest),
                new ParameterizedTypeReference<Collection<SearchFailedMessageResponse>>() {
                })
                .getBody();
    }

    private FailedMessageResponse findFailedMessageById(FailedMessageId failedMessageId) {
        return testRestTemplate.getForObject(
                "/core/failed-message/{failedMessageId}",
                FailedMessageResponse.class,
                failedMessageId);
    }


    public SearchFailedMessageThenStage theSearchResultsContain(Matcher<Iterable<? extends SearchFailedMessageResponse>> resultsMatcher) {
        assertThat(searchResponse.getStatusCodeValue(), is(Status.OK.getStatusCode()));
        assertThat(searchResponse.getBody(), resultsMatcher);
        return this;
    }

    public static Matcher<Iterable<? extends SearchFailedMessageResponse>> noResults() {
        return new IsEmptyIterable<SearchFailedMessageResponse>() {
            @Override
            public String toString() {
                return "no results";
            }
        };
    }

}
