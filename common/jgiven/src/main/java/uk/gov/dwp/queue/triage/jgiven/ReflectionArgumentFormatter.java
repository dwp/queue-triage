package uk.gov.dwp.queue.triage.jgiven;

import com.tngtech.jgiven.format.ArgumentFormatter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static uk.gov.dwp.queue.triage.jgiven.ToStringStyleWithFieldsToInclude.toStringWithFields;

public class ReflectionArgumentFormatter implements ArgumentFormatter<Object> {
    @Override
    public String format(Object argumentToFormat, String... fieldsToInclude) {
        if (argumentToFormat instanceof Object[]) {
            return Stream.of((Object[]) argumentToFormat)
                    .map(item -> format(item, fieldsToInclude))
                    .collect(joining(", and "));
        }
        return ReflectionToStringBuilder.toString(argumentToFormat, fieldsToInclude != null ? toStringWithFields(fieldsToInclude) : null);
    }
}