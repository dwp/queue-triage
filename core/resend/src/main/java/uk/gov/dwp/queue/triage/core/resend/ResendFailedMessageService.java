package uk.gov.dwp.queue.triage.core.resend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.client.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.jms.MessageSender;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.newSearchFailedMessageRequest;

public class ResendFailedMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendFailedMessageService.class);

    private final String brokerName;
    private final FailedMessageSearchService failedMessageSearchService;
    private final MessageSender messageSender;

    public ResendFailedMessageService(String brokerName,
                                      FailedMessageSearchService failedMessageSearchService,
                                      MessageSender messageSender) {
        this.brokerName = brokerName;
        this.failedMessageSearchService = failedMessageSearchService;
        this.messageSender = messageSender;
    }

    public void resendMessages() {
        LOGGER.debug("Executing the ResendFailedMessageService for: ", brokerName);
        failedMessageSearchService
                .search(newSearchFailedMessageRequest()
                        .withBroker(brokerName)
                        .withStatus(FailedMessageStatus.FAILED)
                        .build())
                .forEach(messageSender::send);
    }


}
