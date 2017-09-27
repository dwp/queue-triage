package uk.gov.dwp.queue.triage.core.service;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.dao.FailedMessageDao;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.Collections;

import static java.util.Collections.singleton;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FailedMessageLabelServiceTest {

    private static final String LABEL = "foo";
    private static final FailedMessageId FAILED_MESSAGE_ID = FailedMessageId.newFailedMessageId();
    private final FailedMessageDao failedMessageDao = mock(FailedMessageDao.class);
    private final FailedMessageLabelService underTest = new FailedMessageLabelService(failedMessageDao);

    @Test
    public void addLabelDelegatesToDao() throws Exception {
        underTest.addLabel(FAILED_MESSAGE_ID, LABEL);

        verify(failedMessageDao).addLabel(FAILED_MESSAGE_ID, LABEL);
    }

    @Test
    public void removeLabelDelegatesToDao() throws Exception {
        underTest.removeLabel(FAILED_MESSAGE_ID, LABEL);

        verify(failedMessageDao).removeLabel(FAILED_MESSAGE_ID, LABEL);
    }

    @Test
    public void setLabelsDelegatesToDao() {
        underTest.setLabels(FAILED_MESSAGE_ID, singleton(LABEL));

        verify(failedMessageDao).setLabels(FAILED_MESSAGE_ID, singleton(LABEL));
    }
}