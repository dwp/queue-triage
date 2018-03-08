package uk.gov.dwp.queue.triage.core.service.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.function.Function;

public class DisregardingFailedMessageProcessor implements FailedMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DisregardingFailedMessageProcessor.class);

    private final Function<FailedMessage, String> reason;

    public DisregardingFailedMessageProcessor(Function<FailedMessage, String> reason) {
        this.reason = reason;
    }

    public void process(FailedMessage failedMessage) {
        LOGGER.debug("Disregarding FailedMessage: {} {}", failedMessage.getFailedMessageId(), reason.apply(failedMessage));
    }
}
