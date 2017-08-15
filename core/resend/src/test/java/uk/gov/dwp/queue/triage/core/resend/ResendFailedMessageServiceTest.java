package uk.gov.dwp.queue.triage.core.resend;

import org.junit.Test;
import org.mockito.internal.hamcrest.HamcrestArgumentMatcher;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.jms.MessageSender;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.FAILED;
import static uk.gov.dwp.queue.triage.core.domain.SearchFailedMessageRequestMatcher.aSearchRequest;

public class ResendFailedMessageServiceTest {

    private static final String BROKER_NAME = "internal-broker";
    private final FailedMessageSearchService failedMessageSearchService = mock(FailedMessageSearchService.class);
    private final MessageSender messageSender = mock(MessageSender.class);

    private final ResendFailedMessageService underTest = new ResendFailedMessageService(
            BROKER_NAME,
            failedMessageSearchService,
            messageSender
    );
    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final FailedMessage anotherFailedMessage = mock(FailedMessage.class);

    @Test
    public void successfullyResendFailedMessages() throws Exception {
        SearchFailedMessageRequest searchRequest = argThat(new HamcrestArgumentMatcher<>(aSearchRequest()
                .withBroker(equalTo(Optional.of(BROKER_NAME)))
                .withStatusMatcher(contains(FAILED))));

        when(failedMessageSearchService.search(searchRequest)).thenReturn(asList(failedMessage, anotherFailedMessage));

        underTest.resendMessages();

        verify(messageSender).send(failedMessage);
        verify(messageSender).send(anotherFailedMessage);
        verifyNoMoreInteractions(messageSender);
    }
}