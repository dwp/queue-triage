package uk.gov.dwp.queue.triage.core.search;

import org.hamcrest.Matchers;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.BaseCoreComponentTest;
import uk.gov.dwp.queue.triage.core.stage.FailedMessageResourceStage;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageResponseMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class FailedMessageResourceComponentTest extends BaseCoreComponentTest<FailedMessageResourceStage> {

    private static final Instant NOW = Instant.now();

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
                .withFailedAt(equalTo(NOW))
                .withProperties(Matchers.hasEntry("foo", "bar"))
                .withSentAt(equalTo(NOW)));
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
