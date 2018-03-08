package uk.gov.dwp.queue.triage.core.service.processor;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.Collections;

import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static uk.gov.dwp.queue.triage.core.domain.SearchFailedMessageRequestMatcher.aSearchRequestMatchingAllCriteria;

public class UniqueJmsMessageIdPredicateTest {

    private static final String JMS_MESSAGE_ID = "ID:localhost.localdomain-98564-1519823541516-5:1:1:1:1";
    private static final String BROKER_NAME = "internal-broker";

    private final FailedMessageSearchService failedMessageSearchService = mock(FailedMessageSearchService.class);
    private final UniqueJmsMessageIdPredicate underTest = new UniqueJmsMessageIdPredicate(failedMessageSearchService);
    private final Destination destination = mock(Destination.class);
    private final FailedMessage failedMessage = mock(FailedMessage.class);

    @Test
    public void predicateReturnsTrueIfSearchFindsAResult() {
        when(destination.getBrokerName()).thenReturn(BROKER_NAME);
        when(failedMessage.getDestination()).thenReturn(destination);
        when(failedMessage.getJmsMessageId()).thenReturn(JMS_MESSAGE_ID);
        when(failedMessageSearchService.search(any(SearchFailedMessageRequest.class))).thenReturn(Collections.emptySet());

        assertThat(underTest.test(failedMessage), is(true));

        verify(failedMessageSearchService).search(argThat(aSearchRequestMatchingAllCriteria()
                .withBroker(BROKER_NAME)
                .withJmsMessageId(JMS_MESSAGE_ID)));
    }

    @Test
    public void predicateReturnsFalseIfSearchFindsAResult() {
        when(destination.getBrokerName()).thenReturn(BROKER_NAME);
        when(failedMessage.getDestination()).thenReturn(destination);
        when(failedMessage.getJmsMessageId()).thenReturn(JMS_MESSAGE_ID);
        when(failedMessageSearchService.search(any(SearchFailedMessageRequest.class))).thenReturn(singleton(mock(FailedMessage.class)));

        assertThat(underTest.test(failedMessage), is(false));

        verify(failedMessageSearchService).search(argThat(aSearchRequestMatchingAllCriteria()
                .withBroker(BROKER_NAME)
                .withJmsMessageId(JMS_MESSAGE_ID)));
    }
}