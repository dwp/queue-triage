package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status;

import java.util.Set;

import static com.mongodb.QueryOperators.IN;
import static com.mongodb.QueryOperators.NE;
import static java.util.stream.Collectors.toList;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.STATUS_HISTORY;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageStatusDBObjectConverter.STATUS;

public class MongoStatusHistoryQueryBuilder {

    public BasicDBObject currentStatusEqualTo(Status status) {
        return new BasicDBObject(STATUS_HISTORY + ".0." + STATUS, status.name());
    }

    public BasicDBObject currentStatusNotEqualTo(Status status) {
        return new BasicDBObject(STATUS_HISTORY + ".0." + STATUS, new BasicDBObject(NE, status.name()));
    }

    public BasicDBObject currentStatusIn(Set<Status> statuses) {
        return currentStatusIn(new BasicDBObject(), statuses);
    }

    public BasicDBObject currentStatusIn(BasicDBObject query, Set<Status> statuses) {
        return query.append(STATUS_HISTORY + ".0." + STATUS, statusList(statuses));
    }

    private BasicDBObject statusList(Set<Status> statuses) {
        return new BasicDBObject(IN, statuses.stream().map(Enum::name).collect(toList()));
    }
}
