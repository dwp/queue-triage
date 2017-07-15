package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.queue.triage.core.dao.ObjectConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.configuration.DaoConfig;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.of;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DBObjectMatcher.hasField;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.CONTENT;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.DESTINATION;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.PROPERTIES;
import static uk.gov.dwp.queue.triage.core.domain.DestinationMatcher.aDestination;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class FailedMessageConverterTest {

    private static final FailedMessageId FAILED_MESSAGE_ID = newFailedMessageId();
    private static final String FAILED_MESSAGE_ID_AS_STRING = FAILED_MESSAGE_ID.getId().toString();
    private static final Map<String, Object> SOME_PROPERTIES = new HashMap<String, Object>() {{
        put("propertyName", "propertyValue");
    }};
    private static final Destination SOME_DESTINATION = new Destination("broker", of("queue.name"));
    private static final BasicDBObject DESTINATION_DB_OBJECT = new BasicDBObject();
    private static final Instant SENT_AT = Instant.now().minus(5, ChronoUnit.MINUTES);
    private static final Instant FAILED_AT = Instant.now();

    private final DBObjectConverter<Destination> destinationDBObjectConverter = mock(DBObjectConverter.class);
    private final ObjectConverter<Map<String, Object>, String> propertiesConverter = mock(ObjectConverter.class);

    private final FailedMessageConverter underTest = new DaoConfig().failedMessageConverter(destinationDBObjectConverter, propertiesConverter);

    private FailedMessageBuilder failedMessageBuilder;

    @Before
    public void setUp() {
        failedMessageBuilder = FailedMessageBuilder.newFailedMessage()
                .withFailedMessageId(FAILED_MESSAGE_ID)
                .withDestination(SOME_DESTINATION)
                .withContent("Hello")
                .withSentDateTime(SENT_AT)
                .withFailedDateTime(FAILED_AT)
                .withProperties(SOME_PROPERTIES);
    }

    @Test
    public void createId() {
        assertThat(underTest.createId(FAILED_MESSAGE_ID), equalTo(new BasicDBObject("_id", FAILED_MESSAGE_ID_AS_STRING)));
    }

    @Test
    public void mapNullDBObjectToFailedMessage() {
        assertThat(underTest.convertToObject(null), is(nullValue()));
    }

    @Test
    public void convertFailedMessage() {
        primePropertiesConverter(SOME_PROPERTIES, "{ \"propertyName\": \"propertyValue\" }");
        primeDestinationConverter(SOME_DESTINATION, DESTINATION_DB_OBJECT);

        DBObject dbObject = underTest.convertFromObject(failedMessageBuilder.build());
        assertThat(dbObject, allOf(
                hasField("_id", FAILED_MESSAGE_ID_AS_STRING),
                hasField(CONTENT, "Hello"),
                hasField(DESTINATION, DESTINATION_DB_OBJECT),
                hasField(PROPERTIES, "{ \"propertyName\": \"propertyValue\" }")
        ));

        assertThat(underTest.convertToObject(dbObject), is(aFailedMessage()
                .withFailedMessageId(equalTo(FAILED_MESSAGE_ID))
                .withContent(equalTo("Hello"))
                .withDestination(aDestination().withBrokerName("broker").withName("queue.name"))
                .withSentAt(SENT_AT)
                .withFailedAt(FAILED_AT)
                .withProperties(equalTo(SOME_PROPERTIES))
        ));
    }

    private void primeDestinationConverter(Destination destination, BasicDBObject destinationDbObject) {
        when(destinationDBObjectConverter.convertFromObject(destination)).thenReturn(destinationDbObject);
        when(destinationDBObjectConverter.convertToObject(destinationDbObject)).thenReturn(destination);
    }

    private void primePropertiesConverter(Map<String, Object> properties, String propertiesAsJson) {
        when(propertiesConverter.convertFromObject(properties)).thenReturn(propertiesAsJson);
        when(propertiesConverter.convertToObject(propertiesAsJson)).thenReturn(properties);
    }
}