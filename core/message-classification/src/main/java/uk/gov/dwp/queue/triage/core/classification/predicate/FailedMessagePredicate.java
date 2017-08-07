package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.function.Predicate;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "predicate")
public interface FailedMessagePredicate extends Predicate<FailedMessage> {

}
