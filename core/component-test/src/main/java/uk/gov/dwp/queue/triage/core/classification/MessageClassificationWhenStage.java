package uk.gov.dwp.queue.triage.core.classification;

import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import uk.gov.dwp.queue.triage.jgiven.WhenStage;

@JGivenStage
public class MessageClassificationWhenStage extends WhenStage<MessageClassificationWhenStage> {

    @Autowired
    private TestRestTemplate testRestTemplate;

    public MessageClassificationWhenStage theMessageClassificationJobExecutes() {
        testRestTemplate.postForLocation(
                "/core/admin/executor/message-classification/execute",
                HttpEntity.EMPTY
        );
        return this;
    }
}
