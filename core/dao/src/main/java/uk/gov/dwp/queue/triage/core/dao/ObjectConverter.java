package uk.gov.dwp.queue.triage.core.dao;

public interface ObjectConverter<T, F> {

    T convertToObject(F dbObject);

    F convertFromObject(T item);
}
