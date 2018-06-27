package uk.gov.dwp.queue.triage.core.classification;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import uk.gov.dwp.queue.triage.core.classification.client.MessageClassificationOutcomeResponse;
import uk.gov.dwp.queue.triage.id.FailedMessageId;
import uk.gov.dwp.queue.triage.jgiven.WhenStage;

import java.util.Collections;

@JGivenStage
public class MessageClassificationWhenStage extends WhenStage<MessageClassificationWhenStage> {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @ProvidedScenarioState
    private MessageClassificationOutcomeResponse messageClassificationOutcome;

    public MessageClassificationWhenStage theMessageClassificationJobExecutes() {
        testRestTemplate.postForLocation(
                "/core/admin/executor/message-classification/execute",
                HttpEntity.EMPTY
        );
        return this;
    }

    public MessageClassificationWhenStage failedMessage$IsClassified(FailedMessageId failedMessageId) {
        messageClassificationOutcome = testRestTemplate.exchange(
                "/core/message-classification/classify/{failedMessageId}",
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                MessageClassificationOutcomeResponse.class,
                Collections.singletonMap("failedMessageId", failedMessageId)
        ).getBody();
        return self();
    }
}
