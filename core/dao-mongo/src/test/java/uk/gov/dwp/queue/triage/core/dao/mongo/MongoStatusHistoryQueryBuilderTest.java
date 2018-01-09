package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import org.junit.Test;

import static com.google.common.collect.Sets.immutableEnumSet;
import static com.mongodb.QueryOperators.IN;
import static com.mongodb.QueryOperators.NE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DBObjectMatcher.hasField;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.STATUS_HISTORY;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageStatusDBObjectConverter.STATUS;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.CLASSIFIED;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.DELETED;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.FAILED;

public class MongoStatusHistoryQueryBuilderTest {

    private final MongoStatusHistoryQueryBuilder underTest = new MongoStatusHistoryQueryBuilder();

    @Test
    public void currentStatusEqualToGivenStatus() throws Exception {
        assertThat(underTest.currentStatusEqualTo(FAILED),
                hasField(STATUS_HISTORY + ".0." + STATUS, equalTo(FAILED.name())));
    }

    @Test
    public void currentStatusIsOneOfTheGivenStatuses() {
        assertThat(underTest.currentStatusIn(immutableEnumSet(FAILED, CLASSIFIED)),
                hasField(STATUS_HISTORY + ".0." + STATUS, hasField(IN, contains(FAILED.name(), CLASSIFIED.name()))));
    }

    @Test
    public void currentStatusIsOneOfTheGivenStatusesWhenDBObjectPassedIn() {
        BasicDBObject basicDBObject = new BasicDBObject();

        DBObjectMatcher expectedDBObject = hasField(STATUS_HISTORY + ".0." + STATUS, hasField(IN, contains(FAILED.name(), CLASSIFIED.name())));

        BasicDBObject actualDBObject = underTest.currentStatusIn(basicDBObject, immutableEnumSet(FAILED, CLASSIFIED));
        assertThat(actualDBObject, expectedDBObject);
        assertThat(actualDBObject, is(basicDBObject));
    }

    @Test
    public void currentStatusNotEqualTo() {
        assertThat(underTest.currentStatusNotEqualTo(DELETED),
                hasField(STATUS_HISTORY + ".0." + STATUS, hasField(NE, equalTo(DELETED.name()))));
    }
}