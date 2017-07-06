package uk.gov.dwp.queue.triage.core.health;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

@EnableJGiven
@SpringBootTest(webEnvironment= RANDOM_PORT)
public class FailedMessageResourceComponentTest extends SimpleSpringRuleScenarioTest<FailedMessageResourceStage> {

    private static final ZonedDateTime NOW = ZonedDateTime.now();

    private final FailedMessageId failedMessageId = newFailedMessageId();

    @Test
    public void findMessageByIdThatExists() throws Exception {
        given().aFailedMessage()
                .withFailedMessageId(failedMessageId)
                .withContent("Hello World")
                .withDestination("broker", "queue")
                .withFailedDateTime(NOW)
                .withProperties(Collections.singletonMap("foo", "bar"))
                .withSentDateTime(NOW)
                .exists();

        when().aMessageWithId$IsSelected(failedMessageId);

        then().thenTheFailedMessageReturned(aFailedMessage()
                .withFailedMessageId(equalTo(failedMessageId))
                .withContent(equalTo("Hello World"))
                .withBroker(equalTo("broker"))
                .withDestination(equalTo(Optional.of("queue")))
                .withFailedAt(equalTo(NOW.withZoneSameInstant(ZoneId.of("UTC"))))
                .withProperties(Matchers.hasEntry("foo", "bar"))
                .withSentAt(equalTo(NOW.withZoneSameInstant(ZoneId.of("UTC")))));
    }

    @Test
    public void findMessageByIdThatDoesNotExist() throws Exception {
        given().aFailedMessage()
                .withFailedMessageId(failedMessageId)
                .doesNotExist();

        when().aMessageWithId$IsSelected(failedMessageId);

        then().noDataIsReturned();
    }
}
