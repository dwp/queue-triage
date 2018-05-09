package uk.gov.dwp.queue.triage.core.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageNotFoundException;
import uk.gov.dwp.queue.triage.core.domain.update.adapter.UpdateRequestAdapterRegistry;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.Optional;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.DELETED;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.FAILED;

public class FailedMessageBuilderFactoryTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final UpdateRequestAdapterRegistry updateRequestAdapterRegistry = mock(UpdateRequestAdapterRegistry.class);
    private final FailedMessage failedMessage = mock(FailedMessage.class);
    private final FailedMessageBuilder failedMessageBuilder = mock(FailedMessageBuilder.class);
    private final FailedMessageDao failedMessageDao = mock(FailedMessageDao.class);

    private final FailedMessageBuilderFactory underTest = new FailedMessageBuilderFactory(updateRequestAdapterRegistry, failedMessageDao) {
        @Override
        protected Function<FailedMessage, FailedMessageBuilder> createFailedMessageBuilder() {
            return actualFailedMessage -> {
                assertThat(actualFailedMessage, is(failedMessage));
                return failedMessageBuilder;
            };
        }
    };

    @Test
    public void failedMessageNotFoundExceptionThrownWhenFailedMessageEmpty() {
        expectedException.expect(FailedMessageNotFoundException.class);
        expectedException.expectMessage("Failed Message: " + FAILED_MESSAGE_ID + " not found");
        when(failedMessageDao.findById(FAILED_MESSAGE_ID)).thenReturn(Optional.empty());

        underTest.create(FAILED_MESSAGE_ID);

        verifyZeroInteractions(failedMessageBuilder);
    }

    @Test
    public void failedMessageNotFoundExceptionThrownIfStatusIsDeleted() {
        expectedException.expect(FailedMessageNotFoundException.class);
        expectedException.expectMessage("Failed Message: " + FAILED_MESSAGE_ID + " not found");
        when(failedMessageDao.findById(FAILED_MESSAGE_ID)).thenReturn(Optional.of(failedMessage));
        when(failedMessage.getStatus()).thenReturn(DELETED);

        underTest.create(FAILED_MESSAGE_ID);

        verifyZeroInteractions(failedMessageBuilder);
    }

    @Test
    public void successfullyCreateAFailedMessageBuilder() {
        when(failedMessageDao.findById(FAILED_MESSAGE_ID)).thenReturn(Optional.of(failedMessage));
        when(failedMessage.getStatus()).thenReturn(FAILED);
        when(failedMessageBuilder.withUpdateRequestAdapterRegistry(updateRequestAdapterRegistry)).thenReturn(failedMessageBuilder);

        assertThat(underTest.create(FAILED_MESSAGE_ID), is(failedMessageBuilder));

        verify(failedMessageBuilder).withUpdateRequestAdapterRegistry(updateRequestAdapterRegistry);
    }
}