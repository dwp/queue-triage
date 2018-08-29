package uk.gov.dwp.queue.triage.core.classification.classifier;

public interface Description<T> {

    Description<T> append(Description description);

    Description<T> append(String description);

    Description<T> append(Object object);

    T getOutput();
}
