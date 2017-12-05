package uk.gov.dwp.queue.triage.web.server.list;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;
import uk.gov.dwp.queue.triage.web.server.search.SearchW2UIRequest;
import uk.gov.dwp.queue.triage.web.server.search.SearchW2UIRequest.Criteria;
import uk.gov.dwp.queue.triage.web.server.search.SearchW2UIRequest.Criteria.Field;
import uk.gov.dwp.queue.triage.web.server.search.SearchW2UIRequest.Criteria.Operator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static uk.gov.dwp.queue.triage.web.server.list.SearchW2UIRequestTest.CriteriaMatcher.criteria;
import static uk.gov.dwp.queue.triage.web.server.search.SearchW2UIRequest.Criteria.Field.BROKER;
import static uk.gov.dwp.queue.triage.web.server.search.SearchW2UIRequest.Criteria.Field.CONTENT;
import static uk.gov.dwp.queue.triage.web.server.search.SearchW2UIRequest.Criteria.Field.DESTINATION;
import static uk.gov.dwp.queue.triage.web.server.search.SearchW2UIRequest.Criteria.Operator.BEGINS;
import static uk.gov.dwp.queue.triage.web.server.search.SearchW2UIRequest.Criteria.Operator.CONTAINS;
import static uk.gov.dwp.queue.triage.web.server.search.SearchW2UIRequest.Criteria.Operator.ENDS;
import static uk.gov.dwp.queue.triage.web.server.search.SearchW2UIRequest.Logic.OR;

public class SearchW2UIRequestTest {

    private static final ObjectMapper OBJECT_MAPPER = new JacksonConfiguration().objectMapper();

    @Test
    public void canDeserialiseSearchW2UIRequest() throws Exception {
        SearchW2UIRequest searchW2UIRequest = OBJECT_MAPPER.readValue("{\n" +
                "  \"cmd\": \"get\",\n" +
                "  \"selected\": [],\n" +
                "  \"limit\": 100,\n" +
                "  \"offset\": 0,\n" +
                "  \"search\": [\n" +
                "    {\n" +
                "      \"field\": \"broker\",\n" +
                "      \"type\": \"text\",\n" +
                "      \"operator\": \"begins\",\n" +
                "      \"value\": \"Lorem\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"field\": \"destination\",\n" +
                "      \"type\": \"text\",\n" +
                "      \"operator\": \"contains\",\n" +
                "      \"value\": \"ipsum\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"field\": \"content\",\n" +
                "      \"type\": \"text\",\n" +
                "      \"operator\": \"ends\",\n" +
                "      \"value\": \"dolor\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"searchLogic\": \"OR\"\n" +
                "}", SearchW2UIRequest.class);

        assertThat(searchW2UIRequest.getSearchLogic(), equalTo(OR));
        assertThat(searchW2UIRequest.getSearchCriteria(), contains(
                criteria().withField(BROKER).withOperator(BEGINS).withValue("Lorem"),
                criteria().withField(DESTINATION).withOperator(CONTAINS).withValue("ipsum"),
                criteria().withField(CONTENT).withOperator(ENDS).withValue("dolor")
        ));
    }

    public static class CriteriaMatcher extends TypeSafeMatcher<Criteria> {

        private Matcher<Field> field = isA(Field.class);
        private Matcher<Operator> operator = isA(Operator.class);
        private Matcher<String> value = isA(String.class);

        private CriteriaMatcher() {
        }

        public static CriteriaMatcher criteria() {
            return new CriteriaMatcher();
        }

        public CriteriaMatcher withField(Field field) {
            this.field = equalTo(field);
            return this;
        }

        public CriteriaMatcher withOperator(Operator operator) {
            this.operator = equalTo(operator);
            return this;
        }

        public CriteriaMatcher withValue(String value) {
            this.value = equalTo(value);
            return this;
        }

        @Override
        protected boolean matchesSafely(Criteria criteria) {
            return field.matches(criteria.getField()) &&
                    operator.matches(criteria.getOperator()) &&
                    value.matches(criteria.getValue())
                    ;
        }

        @Override
        public void describeTo(Description description) {
            description
                    .appendText("field ").appendDescriptionOf(field)
                    .appendText(" operator ").appendDescriptionOf(operator)
                    .appendText(" value ").appendDescriptionOf(value)
                    ;
        }
    }
}