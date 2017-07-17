package uk.gov.dwp.queue.triage.jgiven;

import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

public class ToStringStyleWithFieldsToInclude extends ToStringStyle {

    private final List<String> fieldsToInclude;

    private ToStringStyleWithFieldsToInclude(String... fieldsToInclude) {
        this.fieldsToInclude = (fieldsToInclude != null) ? Arrays.asList(fieldsToInclude) : Collections.<String>emptyList();
        setUseClassName(false);
        setUseShortClassName(false);
        setUseIdentityHashCode(false);
        setUseFieldNames(true);
        setContentStart("with ");
        setContentEnd("'");
        setFieldNameValueSeparator(": '");
        setFieldSeparator("' and ");
    }

    @Override
    public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
        if((value != null) && fieldsToInclude.contains(fieldName)){
            Object val = value;
            if (val instanceof Optional) {
                val = ((Optional)value).orElse("empty");
            }
            super.append(buffer, splitCamelCase(fieldName), val, fullDetail);
        }
    }

    public static ToStringStyleWithFieldsToInclude toStringWithFields(String... fieldsToInclude){
        return new ToStringStyleWithFieldsToInclude(fieldsToInclude);
    }

    private static String splitCamelCase(String value) {
        return join(splitByCharacterTypeCamelCase(value), ' ').toLowerCase();
    }
}