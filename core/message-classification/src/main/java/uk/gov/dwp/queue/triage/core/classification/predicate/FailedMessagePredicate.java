package uk.gov.dwp.queue.triage.core.classification.predicate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import uk.gov.dwp.queue.triage.core.domain.FailedMessage;

import java.util.function.Predicate;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "_type"
)
@JsonSubTypes({
        @Type(value = OrPredicate.class, name = "or"),
        @Type(value = AndPredicate.class, name = "and"),
        @Type(value = BrokerEqualsPredicate.class, name = "brokerEqualTo"),
        @Type(value = ContentEqualToPredicate.class, name = "contentEqualTo"),
        @Type(value = DestinationEqualsPredicate.class, name = "destinationEqualTo"),
        @Type(value = PropertyEqualToPredicate.class, name = "propertyEqualTo"),
        @Type(value = PropertyExistsPredicate.class, name = "propertyExists"),
        @Type(value = PropertyMatchesPredicate.class, name = "propertyMatches"),
})
public interface FailedMessagePredicate extends Predicate<FailedMessage> {

}
