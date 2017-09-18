package uk.gov.dwp.queue.triage.core.resend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;
import uk.gov.dwp.queue.triage.core.jms.MessageSender;
import uk.gov.dwp.queue.triage.core.search.FailedMessageSearchService;

import java.util.function.Predicate;

import static uk.gov.dwp.queue.triage.core.client.FailedMessageStatus.RESENDING;
import static uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.newSearchFailedMessageRequest;

public class ResendFailedMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResendFailedMessageService.class);

    private final String brokerName;
    private final FailedMessageSearchService failedMessageSearchService;
    private final MessageSender messageSender;
    private final Predicate<FailedMessage> historicStatusPredicate;

    public ResendFailedMessageService(String brokerName,
                                      FailedMessageSearchService failedMessageSearchService,
                                      MessageSender messageSender,
                                      HistoricStatusPredicate historicStatusPredicate) {
        this.brokerName = brokerName;
        this.failedMessageSearchService = failedMessageSearchService;
        this.messageSender = messageSender;
        this.historicStatusPredicate = historicStatusPredicate;
    }

    public void resendMessages() {
        LOGGER.debug("Resending FailedMessages to: {}", brokerName);
        failedMessageSearchService
                .search(newSearchFailedMessageRequest()
                        .withBroker(brokerName)
                        .withStatus(RESENDING)
                        .build())
                .stream()
                .filter(historicStatusPredicate::test)
                .forEach(messageSender::send);
    }

    public String getBrokerName() {
        return brokerName;
    }
}
