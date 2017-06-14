package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.DBObject;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.client.Destination;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static uk.gov.dwp.queue.triage.core.client.DestinationMatcher.aDestination;

public class DestinationDBObjectConverterTest {

    private final DestinationDBObjectConverter underTest = new DestinationDBObjectConverter();

    @Test
    public void testConvertQueueToDBObjectAndBack() throws Exception {
        DBObject basicDBObject = underTest.convertFromObject(new Destination("broker.name", "queue.name"));

        assertThat(underTest.convertToObject(basicDBObject), is(aDestination().withBrokerName("broker.name").withName("queue.name")));
    }
}