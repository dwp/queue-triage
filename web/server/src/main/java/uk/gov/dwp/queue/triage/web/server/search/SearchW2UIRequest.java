package uk.gov.dwp.queue.triage.web.server.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.SearchFailedMessageRequestBuilder;
import uk.gov.dwp.queue.triage.web.server.w2ui.BaseW2UIRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchW2UIRequest extends BaseW2UIRequest {

    private final List<Criteria> searchCriteria;
    private final Logic searchLogic;

    public SearchW2UIRequest(@JsonProperty("cmd") String cmd,
                             @JsonProperty("limit") Integer limit,
                             @JsonProperty("offset") Integer offset,
                             @JsonProperty("selected") List<String> selected,
                             @JsonProperty("search") List<Criteria> searchCriteria,
                             @JsonProperty("searchLogic") Logic searchLogic) {
        super(cmd, limit, offset, selected);
        this.searchCriteria = searchCriteria;
        this.searchLogic = searchLogic;
    }

    public List<Criteria> getSearchCriteria() {
        return searchCriteria != null ? searchCriteria : Collections.emptyList();
    }

    public Logic getSearchLogic() {
        return searchLogic;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Criteria {

        private final Field field;
        private final Operator operator;
        private final String value;

        public Criteria(@JsonProperty("field") Field field,
                        @JsonProperty("operator") Operator operator,
                        @JsonProperty("value") String value) {
            this.field = field;
            this.operator = operator;
            this.value = value;
        }

        public Field getField() {
            return field;
        }

        public Operator getOperator() {
            return operator;
        }

        public String getValue() {
            return value;
        }

        public void addToSearchRequest(SearchFailedMessageRequestBuilder builder) {
            field.addToSearchRequest(builder, value);
        }

        public enum Field {
            BROKER("broker") {
                @Override
                public void addToSearchRequest(SearchFailedMessageRequestBuilder builder, String value) {
                    builder.withBroker(value);
                }
            },
            DESTINATION("destination") {
                @Override
                public void addToSearchRequest(SearchFailedMessageRequestBuilder builder, String value) {
                    builder.withDestination(value);
                }
            },
            CONTENT("content") {
                @Override
                public void addToSearchRequest(SearchFailedMessageRequestBuilder builder, String value) {
                    builder.withContent(value);
                }
            };

            private final String field;
            private static final Map<String, Field> MAPPING = Stream.of(values()).collect(Collectors.toMap(Field::getField, Function.identity()));

            Field(String field) {
                this.field = field;
            }

            @JsonCreator
            public static Field of(String field) {
                if (!MAPPING.containsKey(field)) {
                    throw new IllegalArgumentException("Cannot find Field of type: " + field);
                }
                return MAPPING.get(field);
            }

            public String getField() {
                return field;
            }

            public abstract void addToSearchRequest(SearchFailedMessageRequestBuilder builder, String value);
        }

        public enum Operator {
            BEGINS("begins"),
            CONTAINS("contains"),
            ENDS("ends"),
            IS("is");

            private final String operator;
            private static final Map<String, Operator> MAPPING = Stream.of(values()).collect(Collectors.toMap(Operator::getOperator, Function.identity()));

            Operator(String operator) {
                this.operator = operator;
            }

            @JsonCreator
            public static Operator of(String operator) {
                if (!MAPPING.containsKey(operator)) {
                    throw new IllegalArgumentException("Cannot find Operator of type: " + operator);
                }
                return MAPPING.get(operator);
            }

            public String getOperator() {
                return operator;
            }
        }
    }

    public enum Logic {
        AND, OR
    }
}
