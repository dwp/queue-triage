package uk.gov.dwp.queue.triage.web.server.api;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.delete.DeleteFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.label.LabelFailedMessageClient;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.web.server.api.DeleteRequest.newDeleteRequest;
import static uk.gov.dwp.queue.triage.web.server.api.LabelRequest.Change.newChange;
import static uk.gov.dwp.queue.triage.web.server.api.LabelRequest.newLabelRequest;

public class FailedMessageChangeResourceTest {

    private static final FailedMessageId FAILED_MESSAGE_1_ID = FailedMessageId.newFailedMessageId();
    private static final FailedMessageId FAILED_MESSAGE_2_ID = FailedMessageId.newFailedMessageId();

    private final LabelFailedMessageClient labelFailedMessageClient = mock(LabelFailedMessageClient.class);
    private final LabelExtractor labelExtractor = mock(LabelExtractor.class);
    private final DeleteFailedMessageClient deleteFailedMessageClient = mock(DeleteFailedMessageClient.class);

    private final FailedMessageChangeResource underTest = new FailedMessageChangeResource(
            labelFailedMessageClient,
            labelExtractor,
            deleteFailedMessageClient);

    private final Set labels1 = mock(Set.class);
    private final Set labels2 = mock(Set.class);

    @Test
    public void updateLabels() throws Exception {
        when(labelExtractor.extractLabels("foo, bar")).thenReturn(labels1);
        when(labelExtractor.extractLabels("black, white")).thenReturn(labels2);

        String result = underTest.updateLabelsOnFailedMessages(
                newLabelRequest()
                        .withChange(newChange().withRecid(FAILED_MESSAGE_1_ID).withLabels("foo, bar"))
                        .withChange(newChange().withRecid(FAILED_MESSAGE_2_ID).withLabels("black, white"))
                        .build()
        );

        assertThat(result, is(equalTo("{ 'status': 'success' }")));
        verify(labelFailedMessageClient).setLabels(FAILED_MESSAGE_1_ID, labels1);
        verify(labelFailedMessageClient).setLabels(FAILED_MESSAGE_2_ID, labels2);

    }

    @Test
    public void deleteFailedMessages() {
        String result = underTest.deleteFailedMessages(
                newDeleteRequest().withSelectedRecords(FAILED_MESSAGE_1_ID, FAILED_MESSAGE_2_ID).build()
        );

        assertThat(result, is(equalTo("{ 'status': 'success' }")));
        verify(deleteFailedMessageClient).deleteFailedMessage(FAILED_MESSAGE_1_ID);
        verify(deleteFailedMessageClient).deleteFailedMessage(FAILED_MESSAGE_2_ID);
    }
}