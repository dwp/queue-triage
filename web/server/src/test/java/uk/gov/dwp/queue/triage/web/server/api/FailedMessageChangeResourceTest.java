package uk.gov.dwp.queue.triage.web.server.api;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.label.LabelFailedMessageClient;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.web.server.api.LabelRequest.Change.ChangeBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.web.server.api.LabelRequest.Change.newChange;

public class FailedMessageChangeResourceTest {

    private static final FailedMessageId FAILED_MESSAGE_1_ID = FailedMessageId.newFailedMessageId();
    private static final FailedMessageId FAILED_MESSAGE_2_ID = FailedMessageId.newFailedMessageId();

    private final LabelFailedMessageClient labelFailedMessageClient = mock(LabelFailedMessageClient.class);
    private final LabelExtractor labelExtractor = mock(LabelExtractor.class);
    private final FailedMessageChangeResource underTest = new FailedMessageChangeResource(
            labelFailedMessageClient,
            labelExtractor
    );
    private final Set labels1 = mock(Set.class);
    private final Set labels2 = mock(Set.class);

    @Test
    public void updateLabels() throws Exception {
        when(labelExtractor.extractLabels("foo, bar")).thenReturn(labels1);
        when(labelExtractor.extractLabels("black, white")).thenReturn(labels2);

        String result = underTest.updateLabelsOnFailedMessages(labelRequest(
                newChange().withRecid(FAILED_MESSAGE_1_ID).withLabels("foo, bar"),
                newChange().withRecid(FAILED_MESSAGE_2_ID).withLabels("black, white")
        ));

        assertThat(result, is(equalTo("{ 'status': 'success' }")));
        verify(labelFailedMessageClient).setLabels(FAILED_MESSAGE_1_ID, labels1);
        verify(labelFailedMessageClient).setLabels(FAILED_MESSAGE_2_ID, labels2);

    }

    private LabelRequest labelRequest(ChangeBuilder...changeBuilders) {
        return new LabelRequest(
                "cmd",
                0,
                0,
                emptyList(),
                Stream.of(changeBuilders).map(ChangeBuilder::build).collect(toList()));
    }
}