package uk.gov.dwp.queue.triage.core.jms.spring;

import org.springframework.jms.core.MessageCreator;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

public interface FailedMessageCreatorFactory {
    MessageCreator create(FailedMessage failedMessage);
}
