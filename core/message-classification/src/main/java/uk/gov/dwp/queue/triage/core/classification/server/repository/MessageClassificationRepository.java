package uk.gov.dwp.queue.triage.core.classification.server.repository;

import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;

import java.util.List;

public interface MessageClassificationRepository {

    void insert(MessageClassifier messageClassifier);

    List<MessageClassifier> findAll();

    void deleteAll();
}
