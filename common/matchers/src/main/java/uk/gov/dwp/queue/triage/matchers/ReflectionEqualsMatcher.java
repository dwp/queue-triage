package uk.gov.dwp.queue.triage.matchers;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public final class ReflectionEqualsMatcher<T> extends TypeSafeDiagnosingMatcher<T> {
    private final String[] excludedFields;

    private final T object;

    private ReflectionEqualsMatcher(T object, String... excludedFields) {
        this.excludedFields = excludedFields;
        this.object = object;
    }

    @Factory
    public static <T> Matcher<T> reflectionEquals(T target, String... excludedFields) {
        return new ReflectionEqualsMatcher<>(target, excludedFields);
    }

    @Override
    protected boolean matchesSafely(T item, Description mismatchDescription) {
        final boolean matches = EqualsBuilder.reflectionEquals(item, object, excludedFields);
        if (!matches) {
            mismatchDescription
                    .appendText("was: ")
                    .appendText(getStringRepresentation(item, excludedFields));
        }
        return matches;
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("equal to: ")
                .appendText(getStringRepresentation(object, excludedFields));
    }

    private String getStringRepresentation(Object o, String... excludedFields) {
        return new ReflectionToStringBuilder(o)
                .setExcludeFieldNames(excludedFields)
                .toString();
    }
}