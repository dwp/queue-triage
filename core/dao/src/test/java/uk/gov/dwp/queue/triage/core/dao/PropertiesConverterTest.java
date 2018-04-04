package uk.gov.dwp.queue.triage.core.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;

public class PropertiesConverterTest {

    private static final UUID SOME_UUID = UUID.fromString("775376da-ce43-4fc6-8c77-09f9cbd7b445");
    private static final LocalDateTime SOME_LOCAL_DATE_TIME = LocalDateTime.of(LocalDate.of(2016, 2, 8), LocalTime.of(14, 43, 0)).with(MILLI_OF_SECOND, 123);
    private static final Date SOME_DATE = Date.from((SOME_LOCAL_DATE_TIME).toInstant(UTC));

    private final ObjectMapper objectMapper = mock(ObjectMapper.class);

    private final PropertiesConverter underTest = new PropertiesConverter(new JacksonConfiguration().objectMapper());

    @Before
    public void setUp() {
        when(objectMapper.copy()).thenReturn(objectMapper);
    }

    @Test
    public void testMapPropertiesWithEmbededHashMap() {
        Map<String, Object> props = ImmutableMap.<String, Object>builder()
                .put("string", "some string")
                .put("localDateTime", SOME_LOCAL_DATE_TIME)
                .put("date", SOME_DATE)
                .put("integer", 1)
                .put("long", 100L)
                .put("uuid", SOME_UUID)
                .build();
        Map<String, Object> properties = new HashMap<>(props);
        properties.put("properties", new HashMap<>(props));

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

    @Test
    public void nullPropertiesReturnsANewHashMap() {
        assertThat(underTest.convertToObject(null), is(equalTo(new HashMap<>())));
    }

    @Test
    public void convertingNullMapToStringReturnsNull() {
        assertThat(underTest.convertFromObject(null), is(nullValue()));
    }

    @Test
    public void nullValueIsReturnedIfTheJsonCannotBeRead() throws IOException {
        when(objectMapper.readValue(any(String.class), any(TypeReference.class))).thenThrow(IOException.class);
        assertThat(new PropertiesConverter(objectMapper).convertToObject("foo"), is(Collections.emptyMap()));
    }

    @Test
    public void nullValueIsReturnedIfTheMapCannotBeParsed() throws Exception {
        when(objectMapper.writeValueAsString(singletonMap("foo", "bar"))).thenThrow(JsonProcessingException.class);

        assertThat(new PropertiesConverter(objectMapper).convertFromObject(singletonMap("foo", "bar")), is(nullValue()));
    }
}