package uk.gov.dwp.queue.triage.core.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.client.update.UpdateRequest;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;
import uk.gov.dwp.queue.triage.core.domain.update.StatusUpdateRequest;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.DELETED;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.RESEND;
import static uk.gov.dwp.queue.triage.core.domain.update.StatusUpdateRequest.statusUpdateRequest;
import static uk.gov.dwp.queue.triage.core.domain.update.StatusUpdateRequestMatcher.aStatusUpdateRequest;

public class FailedMessageServiceTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Rule
    public MockitoRule mockitoJUnit = MockitoJUnit.rule();

    @Mock
    private FailedMessageDao failedMessageDao;
    @Mock
    private FailedMessage failedMessage;
    @Mock
    private FailedMessageBuilder failedMessageBuilder;
    @Mock
    private FailedMessageBuilderFactory failedMessageBuilderFactory;
    @Mock
    private UpdateRequest updateRequest;
    @Captor
    private ArgumentCaptor<List<? extends UpdateRequest>> updateRequests;

    private FailedMessageService underTest
            ;
    @Before
    public void setUp() {
        underTest = new FailedMessageService(failedMessageDao, failedMessageBuilderFactory);
    }

    @Test
    public void createFailedMessageDelegatesToDao() {
        underTest.create(failedMessage);

        Mockito.verify(failedMessageDao).insert(failedMessage);
    }

    @Test
    public void updateStatus() {
        when(failedMessageBuilderFactory.create(FAILED_MESSAGE_ID)).thenReturn(failedMessageBuilder);
        when(failedMessageBuilder.apply(updateRequests.capture())).thenReturn(failedMessageBuilder);
        when(failedMessageBuilder.build()).thenReturn(failedMessage);

        underTest.update(FAILED_MESSAGE_ID, statusUpdateRequest(RESEND));

        verify(failedMessageDao).update(failedMessage);
        assertThat(updateRequests.getValue(), hasSize(1));
        assertThat((StatusUpdateRequest) updateRequests.getValue().get(0), aStatusUpdateRequest(RESEND));
    }

    @Test
    public void deleteFailedMessage() {
        when(failedMessageBuilderFactory.create(FAILED_MESSAGE_ID)).thenReturn(failedMessageBuilder);
        when(failedMessageBuilder.apply(updateRequests.capture())).thenReturn(failedMessageBuilder);
        when(failedMessageBuilder.build()).thenReturn(failedMessage);

        underTest.delete(FAILED_MESSAGE_ID);

        verify(failedMessageDao).update(failedMessage);
        assertThat(updateRequests.getValue(), hasSize(1));
        assertThat((StatusUpdateRequest) updateRequests.getValue().get(0), aStatusUpdateRequest(DELETED));
    }

    @Test
    public void noUpdatesArePerformedIfTheUpdateRequestListIsEmpty() {
        underTest.update(FAILED_MESSAGE_ID, Collections.emptyList());

        verifyZeroInteractions(failedMessageDao);
        verifyZeroInteractions(failedMessageBuilderFactory);
    }

    @Test
    public void updateAFailedMessageSuccessfully() {
        when(failedMessageBuilderFactory.create(FAILED_MESSAGE_ID)).thenReturn(failedMessageBuilder);
        when(failedMessageBuilder.apply(updateRequests.capture())).thenReturn(failedMessageBuilder);
        when(failedMessageBuilder.build()).thenReturn(failedMessage);

        underTest.update(FAILED_MESSAGE_ID, Collections.singletonList(updateRequest));

        verify(failedMessageDao).update(failedMessage);
        assertThat(updateRequests.getValue(), contains(updateRequest));

    }
}