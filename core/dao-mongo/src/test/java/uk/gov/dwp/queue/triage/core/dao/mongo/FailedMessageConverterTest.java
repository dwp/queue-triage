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
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageStatusMatcher;
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
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.LABELS;
import static uk.gov.dwp.queue.triage.core.dao.mongo.FailedMessageConverter.PROPERTIES;
import static uk.gov.dwp.queue.triage.core.domain.DestinationMatcher.aDestination;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageMatcher.aFailedMessage;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.Status.SENT;
import static uk.gov.dwp.queue.triage.core.domain.FailedMessageStatus.failedMessageStatus;
import static uk.gov.dwp.queue.triage.id.FailedMessageId.newFailedMessageId;

public class FailedMessageConverterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private static final FailedMessageId FAILED_MESSAGE_ID = newFailedMessageId();
    private static final String FAILED_MESSAGE_ID_AS_STRING = FAILED_MESSAGE_ID.getId().toString();
    private static final Map<String, Object> SOME_PROPERTIES = new HashMap<String, Object>() {{
        put("propertyName", "propertyValue");
    }};
    private static final Destination SOME_DESTINATION = new Destination("broker", of("queue.name"));
    private static final BasicDBObject DESTINATION_DB_OBJECT = new BasicDBObject();
    private static final FailedMessageStatus SOME_STATUS = failedMessageStatus(SENT);
    private static final BasicDBObject STATUS_DB_OBJECT = new BasicDBObject();
    private static final Instant SENT_AT = Instant.now().minus(5, ChronoUnit.MINUTES);
    private static final Instant FAILED_AT = Instant.now();

    @Mock
    private DBObjectConverter<Destination> destinationDBObjectConverter;
    @Mock
    private DBObjectConverter<FailedMessageStatus> failedMessageStatusDBObjectConverter;
    @Mock
    private ObjectConverter<Map<String, Object>, String> propertiesConverter;

    private FailedMessageConverter underTest;
    private FailedMessageBuilder failedMessageBuilder;

    @Before
    public void setUp() {
        failedMessageBuilder = FailedMessageBuilder.newFailedMessage()
                .withFailedMessageId(FAILED_MESSAGE_ID)
                .withDestination(SOME_DESTINATION)
                .withContent("Hello")
                .withSentDateTime(SENT_AT)
                .withFailedDateTime(FAILED_AT)
                .withProperties(SOME_PROPERTIES)
                .withFailedMessageStatus(SOME_STATUS)
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
        primePropertiesConverter(SOME_PROPERTIES, "{ \"propertyName\": \"propertyValue\" }");
        primeDestinationConverter(SOME_DESTINATION, DESTINATION_DB_OBJECT);
        primeFailedMessageStatusConverter(SOME_STATUS, STATUS_DB_OBJECT);

        DBObject dbObject = underTest.convertFromObject(failedMessageBuilder.build());
        assertThat(dbObject, allOf(
                hasField("_id", equalTo(FAILED_MESSAGE_ID_AS_STRING)),
                hasField(CONTENT, equalTo("Hello")),
                hasField(DESTINATION, equalTo(DESTINATION_DB_OBJECT)),
                hasField(PROPERTIES, equalTo("{ \"propertyName\": \"propertyValue\" }")),
                hasField(LABELS, equalTo(toBasicDBList(singletonList("PR-1234"))))
        ));

        assertThat(underTest.convertToObject(dbObject), is(aFailedMessage()
                .withFailedMessageId(equalTo(FAILED_MESSAGE_ID))
                .withContent(equalTo("Hello"))
                .withDestination(aDestination().withBrokerName("broker").withName("queue.name"))
                .withSentAt(SENT_AT)
                .withFailedAt(FAILED_AT)
                .withProperties(equalTo(SOME_PROPERTIES))
                .withFailedMessageStatus(FailedMessageStatusMatcher.equalTo(SENT).withUpdatedDateTime(notNullValue(Instant.class)))
                .withLabels(contains("PR-1234"))
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

    private void primeFailedMessageStatusConverter(FailedMessageStatus failedMessageStatus, BasicDBObject statusDBObject) {
        when(failedMessageStatusDBObjectConverter.convertFromObject(failedMessageStatus)).thenReturn(statusDBObject);
        when(failedMessageStatusDBObjectConverter.convertToObject(statusDBObject)).thenReturn(failedMessageStatus);
    }
}