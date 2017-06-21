package uk.gov.dwp.queue.triage.core.dao.mongo;

import com.mongodb.DBObject;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class DBObjectMatcher extends TypeSafeMatcher<DBObject> {

    private final String fieldName;
    private final Object fieldValue;

    public DBObjectMatcher(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    @Override
    protected boolean matchesSafely(DBObject item) {
        return fieldValue.equals(item.get(fieldName));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(fieldName).appendText(" is ").appendValue(fieldValue);
    }

    public static DBObjectMatcher hasField(String fieldName, Object fieldValue) {
        return new DBObjectMatcher(fieldName, fieldValue);
    }
}