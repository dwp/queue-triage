package uk.gov.dwp.queue.triage.core.client.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;

public class PropertiesUpdateRequestTest {

    private static ObjectMapper OBJECT_MAPPER = JacksonConfiguration.defaultObjectMapper();

    @Test
    public void testSerialisingAndDeserialisingAPropertiesUpdateRequest() throws IOException {
        String json = OBJECT_MAPPER.writeValueAsString(new PropertiesUpdateRequest(Collections.singleton("some-key"), Collections.singletonMap("foo", "bar")));

        assertThat(json, allOf(
                hasJsonPath("$._type", equalTo("properties")),
                hasJsonPath("$.deletedProperties[0]", equalTo("some-key")),
                hasJsonPath("$.updatedProperties.foo", equalTo("bar"))
        ));

        assertThat(OBJECT_MAPPER.readValue(json, PropertiesUpdateRequest.class), aPropertiesUpdateRequest(Matchers.contains("some-key"), Matchers.hasEntry("foo", "bar")));
    }

    @Test
    public void gettersReturnEmptyCollectionIfConstructedWithNullValues() {
        final PropertiesUpdateRequest underTest = new PropertiesUpdateRequest(null, null);

        assertThat(underTest, aPropertiesUpdateRequest(Matchers.emptyIterable(), Matchers.is(Collections.emptyMap())));
    }

    @Test
    public void gettersReturnACopyOfTheCollection() {
        Set<String> deletedProperties = new HashSet<>();
        Map<String, Object> updatedProperties = new HashMap<>();

        final PropertiesUpdateRequest underTet = new PropertiesUpdateRequest(deletedProperties, updatedProperties);

        assertThat(underTet, aPropertiesUpdateRequest(not(sameInstance(deletedProperties)), not(sameInstance(updatedProperties))));
    }


    private TypeSafeMatcher<PropertiesUpdateRequest> aPropertiesUpdateRequest(Matcher<Iterable<? extends String>> deletedPropertiesMatcher,
                                                                              Matcher<java.util.Map<? extends String, ? extends Object>> updatedPropertiesMatcher) {
        return new TypeSafeMatcher<PropertiesUpdateRequest>() {
            @Override
            protected boolean matchesSafely(PropertiesUpdateRequest item) {
                return deletedPropertiesMatcher.matches(item.getDeletedProperties()) &&
                        updatedPropertiesMatcher.matches(item.getUpdatedProperties());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("deletedProperties is ").appendDescriptionOf(deletedPropertiesMatcher)
                        .appendText(", updatedProperties is ").appendDescriptionOf(updatedPropertiesMatcher);
            }
        };
    }

}