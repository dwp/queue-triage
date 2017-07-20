package uk.gov.dwp.queue.triage.core.dao;

import org.junit.Test;
import uk.gov.dwp.queue.triage.core.dao.util.HashMapBuilder;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static uk.gov.dwp.queue.triage.core.dao.util.HashMapBuilder.newHashMap;

public class PropertiesConverterTest {

    private static final UUID SOME_UUID = UUID.fromString("775376da-ce43-4fc6-8c77-09f9cbd7b445");
    private static final LocalDateTime SOME_LOCAL_DATE_TIME = LocalDateTime.of(LocalDate.of(2016, 2, 8), LocalTime.of(14, 43, 0)).with(MILLI_OF_SECOND, 123);
    private static final Date SOME_DATE = Date.from((SOME_LOCAL_DATE_TIME).toInstant(UTC));

    private final PropertiesConverter underTest = new PropertiesConverter(new JacksonConfiguration().objectMapper());

    @Test
    public void testMapPropertiesWithEmbededHashMap() throws Exception {
        HashMapBuilder<String, Object> hashMapBuilder = newHashMap(String.class, Object.class)
                .put("string", "some string")
                .put("localDateTime", SOME_LOCAL_DATE_TIME)
                .put("date", SOME_DATE)
                .put("integer", 1)
                .put("long", 100L)
                .put("uuid", SOME_UUID);
        HashMap<String, Object> properties = hashMapBuilder.build();
        properties.put("properties", hashMapBuilder.build());

        String json = underTest.convertFromObject(properties);

        assertThat(json, allOf(
                hasJsonPath("$.string", equalTo("some string")),
                hasJsonPath("$.localDateTime", contains("java.time.LocalDateTime", "2016-02-08T14:43:00.123")),
                hasJsonPath("$.date", contains("java.util.Date", "2016-02-08T14:43:00.123Z")),
                hasJsonPath("$.integer", equalTo(1)),
                hasJsonPath("$.long", contains("java.lang.Long", 100)),
                hasJsonPath("$.uuid", contains("java.util.UUID", SOME_UUID.toString())),
                hasJsonPath("$.properties.string", equalTo("some string")),
                hasJsonPath("$.properties.localDateTime", contains("java.time.LocalDateTime", "2016-02-08T14:43:00.123")),
                hasJsonPath("$.properties.date", contains("java.util.Date", "2016-02-08T14:43:00.123Z")),
                hasJsonPath("$.properties.integer", equalTo(1)),
                hasJsonPath("$.properties.long", contains("java.lang.Long", 100)),
                hasJsonPath("$.properties.uuid", contains("java.util.UUID", SOME_UUID.toString()))
        ));

        Map<String, Object> actual = underTest.convertToObject(json);
        assertThat(actual, equalTo(properties));
    }
}