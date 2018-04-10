package uk.gov.dwp.queue.triage.core.dao.mongo;

import org.bson.Document;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class DocumentMatcher extends TypeSafeMatcher<Document> {

    private final String fieldName;
    private final Matcher<? extends Object> fieldValue;

    private DocumentMatcher(String fieldName, Matcher<? extends Object> fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    @Override
    protected boolean matchesSafely(Document item) {
        return fieldValue.matches(item.get(fieldName));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(fieldName).appendText(" is ").appendValue(fieldValue);
    }

    public static DocumentMatcher hasField(String fieldName, Matcher<? extends Object> fieldValue) {
        return new DocumentMatcher(fieldName, fieldValue);
    }
}