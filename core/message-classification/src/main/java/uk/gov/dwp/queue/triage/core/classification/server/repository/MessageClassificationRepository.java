package uk.gov.dwp.queue.triage.core.classification.server.repository;

import uk.gov.dwp.queue.triage.core.classification.classifier.MessageClassifier;

public interface MessageClassificationRepository {

    void save(MessageClassifier messageClassifier);

    MessageClassifier findLatest();

    void deleteAll();
}
