package uk.gov.dwp.queue.triage.web.component.labels;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.dwp.queue.triage.core.client.label.LabelFailedMessageClient;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.ThenStage;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

@JGivenStage
public class LabelManagementThenStage extends ThenStage<LabelManagementThenStage> {

    @Autowired
    private LabelFailedMessageClient labelFailedMessageClient;

    public LabelManagementThenStage failedMessage$IsUpdatedWithLabels$(FailedMessageId failedMessageId, String...labels) {
        Mockito.verify(labelFailedMessageClient).setLabels(failedMessageId, new HashSet<>(Arrays.asList(labels)));
        return this;
    }

    public LabelManagementThenStage failedMessage$IsUpdatedWithNoLabels(FailedMessageId failedMessageId) {
        Mockito.verify(labelFailedMessageClient).setLabels(failedMessageId, Collections.emptySet());
        return this;
    }

    public LabelManagementThenStage failedMessage$IsNotUpdated(FailedMessageId failedMessageId) {
        Mockito.verify(labelFailedMessageClient, Mockito.never()).setLabels(Mockito.eq(failedMessageId), Mockito.anySet());
        return this;
    }
}
