package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import uk.gov.dwp.queue.triage.core.dao.ObjectConverter;
import uk.gov.dwp.queue.triage.core.dao.mongo.configuration.MongoDaoConfig;
import uk.gov.dwp.queue.triage.core.domain.Destination;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent;
import uk.gov.dwp.queue.triage.core.domain.StatusHistoryEventMatcher;
import uk.gov.dwp.queue.triage.id.FailedMessageId;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.Optional.of;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DBObjectConverter.toBasicDBList;
import static uk.gov.dwp.queue.triage.core.dao.mongo.DBObjectMatcher.hasField;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.CONTENT;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.DESTINATION;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.JMS_MESSAGE_ID;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.LABELS;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.PROPERTIES;
import static uk.gov.dwp.queue.triage.core.domain.DestinationMatcher.aDestination;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.Status.SENT;
import static uk.gov.dwp.queue.triage.core.domain.StatusHistoryEvent.statusHistoryEvent;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class FailedMessageConverterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final FailedMessageId FAILED_MESSAGE_ID = newFailedMessageId();
    private static final String FAILED_MESSAGE_ID_AS_STRING = FAILED_MESSAGE_ID.getId().toString();
    private static final String JMS_MESSAGE_ID_VALUE = "ID:localhost.localdomain-46765-1518703251379-5:1:1:1:1";
    private static final Map<String, Object> SOME_PROPERTIES = new HashMap<String, Object>() {{
        put("propertyName", "propertyValue");
    }};
    private static final Destination SOME_DESTINATION = new Destination("broker", of("queue.name"));
    private static final BasicDBObject DESTINATION_DB_OBJECT = new BasicDBObject();
    private static final StatusHistoryEvent SOME_STATUS = statusHistoryEvent(SENT);
    private static final BasicDBObject STATUS_DB_OBJECT = new BasicDBObject();
    private static final Instant SENT_AT = Instant.now().minus(5, ChronoUnit.MINUTES);
    private static final Instant FAILED_AT = Instant.now();

    @Mock
    private DBObjectConverter<Destination> destinationDBObjectConverter;
    @Mock
    private DBObjectConverter<StatusHistoryEvent> failedMessageStatusDBObjectConverter;
    @Mock
    private ObjectConverter<Map<String, Object>, String> propertiesConverter;

    private FailedMessageConverter underTest;
    private FailedMessageBuilder failedMessageBuilder;

    @Before
    public void setUp() {
        failedMessageBuilder = FailedMessageBuilder.newFailedMessage()
                .withFailedMessageId(FAILED_MESSAGE_ID)
                .withJmsMessageId(JMS_MESSAGE_ID_VALUE)
                .withDestination(SOME_DESTINATION)
                .withContent("Hello")
                .withSentDateTime(SENT_AT)
                .withFailedDateTime(FAILED_AT)
                .withProperties(SOME_PROPERTIES)
                .withStatusHistoryEvent(SOME_STATUS)
                .withLabel("PR-1234")
        ;
        underTest = new MongoDaoConfig().failedMessageConverter(
                destinationDBObjectConverter,
                failedMessageStatusDBObjectConverter,
                propertiesConverter
        );
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
        when(propertiesConverter.convertFromObject(SOME_PROPERTIES)).thenReturn("{ \"propertyName\": \"propertyValue\" }");
        when(destinationDBObjectConverter.convertFromObject(SOME_DESTINATION)).thenReturn(DESTINATION_DB_OBJECT);
        primeFailedMessageStatusConverter(SOME_STATUS, STATUS_DB_OBJECT);

        DBObject dbObject = underTest.convertFromObject(failedMessageBuilder.build());
        assertThat(dbObject, allOf(
                hasField("_id", equalTo(FAILED_MESSAGE_ID_AS_STRING)),
                hasField(JMS_MESSAGE_ID, equalTo(JMS_MESSAGE_ID_VALUE)),
                hasField(CONTENT, equalTo("Hello")),
                hasField(DESTINATION, equalTo(DESTINATION_DB_OBJECT)),
                hasField(PROPERTIES, equalTo("{ \"propertyName\": \"propertyValue\" }")),
                hasField(LABELS, equalTo(toBasicDBList(singletonList("PR-1234"))))
        ));

        when(propertiesConverter.convertToObject("{ \"propertyName\": \"propertyValue\" }")).thenReturn(SOME_PROPERTIES);
        when(destinationDBObjectConverter.convertToObject(DESTINATION_DB_OBJECT)).thenReturn(SOME_DESTINATION);
        assertThat(underTest.convertToObject(dbObject), is(aFailedMessage()
                .withFailedMessageId(equalTo(FAILED_MESSAGE_ID))
                .withJmsMessageId(equalTo(JMS_MESSAGE_ID_VALUE))
                .withContent(equalTo("Hello"))
                .withDestination(aDestination().withBrokerName("broker").withName("queue.name"))
                .withSentAt(SENT_AT)
                .withFailedAt(FAILED_AT)
                .withProperties(equalTo(SOME_PROPERTIES))
                .withFailedMessageStatus(StatusHistoryEventMatcher.equalTo(SENT).withUpdatedDateTime(notNullValue(Instant.class)))
                .withLabels(contains("PR-1234"))
        ));
    }

    @Test
    public void convertFailedMessageForUpdate() {
        when(propertiesConverter.convertFromObject(SOME_PROPERTIES)).thenReturn("{ \"propertyName\": \"propertyValue\" }");
        when(destinationDBObjectConverter.convertFromObject(SOME_DESTINATION)).thenReturn(DESTINATION_DB_OBJECT);

        assertThat(underTest.convertForUpdate(failedMessageBuilder.build()), allOf(
                hasField(JMS_MESSAGE_ID, equalTo(JMS_MESSAGE_ID_VALUE)),
                hasField(CONTENT, equalTo("Hello")),
                hasField(DESTINATION, equalTo(DESTINATION_DB_OBJECT)),
                hasField(PROPERTIES, equalTo("{ \"propertyName\": \"propertyValue\" }"))
        ));
    }

    private void primeFailedMessageStatusConverter(StatusHistoryEvent statusHistoryEvent, BasicDBObject statusDBObject) {
        when(failedMessageStatusDBObjectConverter.convertFromObject(statusHistoryEvent)).thenReturn(statusDBObject);
        when(failedMessageStatusDBObjectConverter.convertToObject(statusDBObject)).thenReturn(statusHistoryEvent);
    }
}