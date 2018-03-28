package uk.gov.dwp.queue.triage.web.server.search;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.web.server.search.SearchW2UIRequest.Criteria;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequestMatcher.aSearchRequestMatchingAnyCriteria;

public class SearchFailedMessageRequestAdapterTest {

    private final SearchW2UIRequest searchW2UIRequest = mock(SearchW2UIRequest.class);
    private final SearchFailedMessageRequestAdapter underTest = new SearchFailedMessageRequestAdapter();

    @Test
    public void searchWithMultipleCriteria() throws Exception {
        when(searchW2UIRequest.getSearchCriteria()).thenReturn(Arrays.asList(
                new Criteria(Criteria.Field.BROKER, Criteria.Operator.BEGINS, "Lorem"),
                new Criteria(Criteria.Field.CONTENT, Criteria.Operator.ENDS, "Ipsum"),
                new Criteria(Criteria.Field.DESTINATION, Criteria.Operator.CONTAINS, "Dolor")
        ));

        SearchFailedMessageRequest request = underTest.adapt(searchW2UIRequest);

        assertThat(request, aSearchRequestMatchingAnyCriteria()
                .withBroker("Lorem")
                .withContent("Ipsum")
                .withDestination("Dolor"));
    }

    @Test
    public void searchWithNoCriteria() throws Exception {
        when(searchW2UIRequest.getSearchCriteria()).thenReturn(Collections.emptyList());

        SearchFailedMessageRequest request = underTest.adapt(searchW2UIRequest);

        assertThat(request, aSearchRequestMatchingAnyCriteria()
                .withBroker(equalTo(Optional.empty()))
                .withContent(equalTo(Optional.empty()))
                .withDestination(equalTo(Optional.empty())));
    }
}