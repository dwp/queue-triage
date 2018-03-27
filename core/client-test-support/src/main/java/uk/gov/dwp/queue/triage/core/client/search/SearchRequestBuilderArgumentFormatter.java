package uk.gov.dwp.queue.triage.core.client.search;

import com.tngtech.jgiven.format.ArgumentFormatter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.Operator;
import uk.gov.dwp.queue.triage.core.client.search.SearchFailedMessageRequest.SearchFailedMessageRequestBuilder;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Optional;

public class SearchRequestBuilderArgumentFormatter implements ArgumentFormatter<SearchFailedMessageRequestBuilder> {

    @Override
    public String format(SearchFailedMessageRequestBuilder builder, String... strings) {
        try {
            final Field operatorField = builder.getClass().getDeclaredField("operator");
            operatorField.setAccessible(true);
            final Operator operator = (Operator) operatorField.get(builder);
            return ReflectionToStringBuilder.toString(
                    builder,
                    new SearchRequestBuilderToStringStyle(operator.name().toLowerCase())
            );
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot obtain Field operator");
        }

    }

    static class SearchRequestBuilderToStringStyle extends ToStringStyle {

        public SearchRequestBuilderToStringStyle(String fieldSeperator) {
            setUseClassName(false);
            setUseIdentityHashCode(false);
            setContentStart("with ");
            setContentEnd("'");
            setFieldNameValueSeparator(": '");
            setFieldSeparator("' " + fieldSeperator + " with ");
        }

        @Override
        public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
            if (value == null) {
                return;
            }
            if (value instanceof Optional) {
                ((Optional)value).ifPresent(o -> super.append(buffer, fieldName, o, fullDetail));
                return;
            } else if (value instanceof Collection && ((Collection)value).isEmpty()) {
                return;
            }
            super.append(buffer, fieldName, value, fullDetail);
        }

    }
}
