package uk.gov.dwp.queue.triage.core.classification.action;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.function.Consumer;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "action")
public interface FailedMessageAction extends Consumer<FailedMessage> {
}
