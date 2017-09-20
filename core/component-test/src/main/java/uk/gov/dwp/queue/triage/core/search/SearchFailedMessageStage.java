package uk.gov.dwp.queue.triage.core.search;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.hamcrest.Matcher;
import org.hamcrest.collection.IsEmptyIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.SearchFailedMessageRequestBuilder;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.jgiven.ReflectionArgumentFormatter;

import javax.ws.rs.core.Response.Status;
import java.util.Collection;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@JGivenStage
public class SearchFailedMessageStage extends Stage<SearchFailedMessageStage> {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @ExpectedScenarioState
    private ResponseEntity<Collection<SearchFailedMessageResponse>> searchResponse;

    public SearchFailedMessageStage aSearchIsRequested(@Format(value = ReflectionArgumentFormatter.class, args = {"broker", "destination"}) SearchFailedMessageRequestBuilder requestBuilder) {
        searchResponse = testRestTemplate.exchange(
                "/core/failed-message/search",
                HttpMethod.POST,
                new HttpEntity<>(requestBuilder.build()),
                new ParameterizedTypeReference<Collection<SearchFailedMessageResponse>>() {
                });
        return this;
    }

    public SearchFailedMessageStage a$HttpResponseCodeIsReceived(Status statusCode) {
        assertThat(searchResponse.getStatusCodeValue(), is(statusCode.getStatusCode()));
        return this;
    }

    public SearchFailedMessageStage aSearch$WillContain$(
            @Format(value = ReflectionArgumentFormatter.class, args = {"broker", "destination"}) SearchFailedMessageRequestBuilder requestBuilder,
            Matcher<Iterable<? extends SearchFailedMessageResponse>> resultsMatcher
    ) {
        await().atMost(5, SECONDS)
                .until(testRestTemplate.exchange(
                                "/core/failed-message/search",
                                HttpMethod.POST,
                                new HttpEntity<>(requestBuilder.build()),
                                new ParameterizedTypeReference<Collection<SearchFailedMessageResponse>>() {})::getBody,
                       resultsMatcher);
        return this;
    }

    public SearchFailedMessageStage theSearchResultsContain(Matcher<Iterable<? extends SearchFailedMessageResponse>> resultsMatcher) {
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
