package uk.gov.dwp.queue.triage.core.dao.mongo;

import org.bson.Document;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status;

import java.util.Set;

import static com.mongodb.QueryOperators.IN;
import static com.mongodb.QueryOperators.NE;
import static java.util.stream.Collectors.toList;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.STATUS_HISTORY;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageStatusDocumentConverter.STATUS;

public class MongoStatusHistoryQueryBuilder {

    public Document currentStatusEqualTo(Status status) {
        return new Document(STATUS_HISTORY + ".0." + STATUS, status.name());
    }

    public Document currentStatusNotEqualTo(Status status) {
        return new Document(STATUS_HISTORY + ".0." + STATUS, new Document(NE, status.name()));
    }

    public Document currentStatusIn(Set<Status> statuses) {
        return currentStatusIn(new Document(), statuses);
    }

    public Document currentStatusIn(Document query, Set<Status> statuses) {
        return query.append(STATUS_HISTORY + ".0." + STATUS, statusList(statuses));
    }

    private Document statusList(Set<Status> statuses) {
        return new Document(IN, statuses.stream().map(Enum::name).collect(toList()));
    }
}
