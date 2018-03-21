package uk.gov.dwp.queue.triage.core.search;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

@EnableJGiven
@SpringBootTest(webEnvironment= RANDOM_PORT)
public class FailedMessageResourceComponentTest extends BaseCoreComponentTest<FailedMessageResourceStage> {

    private static final Instant NOW = Instant.now();

    private final FailedMessageId failedMessageId = newFailedMessageId();

    @Test
    public void findMessageByIdThatExists() {
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
                .withFailedAt(equalTo(NOW))
                .withProperties(Matchers.hasEntry("foo", "bar"))
                .withSentAt(equalTo(NOW)));
    }

    @Test
    public void findMessageByIdThatDoesNotExist() {
        given().aFailedMessage()
                .withFailedMessageId(failedMessageId)
                .doesNotExist();

        when().aMessageWithId$IsSelected(failedMessageId);

        then().noDataIsReturned();
    }
}
