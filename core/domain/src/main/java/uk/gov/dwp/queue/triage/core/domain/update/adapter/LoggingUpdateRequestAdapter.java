package uk.gov.dwp.queue.triage.core.domain.update.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.client.update.UpdateRequest;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;

public class LoggingUpdateRequestAdapter implements UpdateRequestAdapter<UpdateRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingUpdateRequestAdapter.class);

    @Override
    public void adapt(UpdateRequest updateRequest, FailedMessageBuilder failedMessageBuilder) {
        getLogger().warn("UpdateRequestAdapter not found for: {}", updateRequest.getClass().getName());
    }

    protected Logger getLogger() {
        return LOGGER;
    }
}
