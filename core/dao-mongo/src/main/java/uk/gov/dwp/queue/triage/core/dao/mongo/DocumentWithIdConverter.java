package uk.gov.dwp.queue.triage.core.dao.mongo;

import org.bson.Document;

public interface DocumentWithIdConverter<T, ID> extends DocumentConverter<T> {

    Document createId(ID id);
}