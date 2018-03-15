package uk.gov.dwp.queue.triage.core.resource.search;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import uk.gov.dwp.queue.triage.core.client.FailedMessageResponse;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageResponse;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;
import uk.gov.dwp.queue.triage.core.search.SearchFailedMessageResponseAdapter;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import javax.ws.rs.NotFoundException;
import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class FailedMessageSearchResourceTest {

    @Rule
    public ExpectedException expectedException = none();

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();

    private final FailedMessageResponseFactory failedMessageResponseFactory = mock(FailedMessageResponseFactory.class);
    private final FailedMessageDao failedMessageDao = mock(FailedMessageDao.class);
    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final FailedMessageResponse failedMessageResponse = mock(FailedMessageResponse.class);
    private final FailedMessageSearchService failedMessageSearchService = mock(FailedMessageSearchService.class);
    private final SearchFailedMessageResponseAdapter searchFailedMessageResponseAdapter = mock(SearchFailedMessageResponseAdapter.class);

    private final FailedMessageSearchResource underTest = new FailedMessageSearchResource(
            failedMessageDao,
            failedMessageResponseFactory,
            failedMessageSearchService,
            searchFailedMessageResponseAdapter
    );

    @Test
    public void findMessageById() throws Exception {
        when(failedMessageDao.findById(FAILED_MESSAGE_ID)).thenReturn(failedMessage);
        when(failedMessageResponseFactory.create(failedMessage)).thenReturn(failedMessageResponse);

        assertThat(underTest.getFailedMessage(FAILED_MESSAGE_ID), is(failedMessageResponse));
    }

    @Test
    public void findMessageByIdThrowsNotFoundException() throws Exception {
        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Failed Message: " + FAILED_MESSAGE_ID + " not found");
        when(failedMessageDao.findById(FAILED_MESSAGE_ID)).thenReturn(null);

        underTest.getFailedMessage(FAILED_MESSAGE_ID);

        verifyZeroInteractions(failedMessageResponseFactory);
    }

    @Test
    public void validSearch() {
        FailedMessage failedMessage = mock(FailedMessage.class);
        SearchFailedMessageResponse searchFailedMessageResponse = mock(SearchFailedMessageResponse.class);
        SearchFailedMessageRequest searchFailedMessageRequest = mock(SearchFailedMessageRequest.class);
        when(searchFailedMessageRequest.getBroker()).thenReturn(Optional.of("broker"));

        when(failedMessageSearchService.search(searchFailedMessageRequest)).thenReturn(singletonList(failedMessage));
        when(searchFailedMessageResponseAdapter.toResponse(failedMessage)).thenReturn(searchFailedMessageResponse);

        final Collection<SearchFailedMessageResponse> results = underTest.search(searchFailedMessageRequest);

        assertThat(results, contains(searchFailedMessageResponse));
        verify(failedMessageSearchService).search(Mockito.refEq(searchFailedMessageRequest));
    }
}