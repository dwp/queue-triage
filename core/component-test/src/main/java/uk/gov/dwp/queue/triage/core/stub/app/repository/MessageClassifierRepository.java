package uk.gov.dwp.queue.triage.core.stub.app.repository;

import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;

import java.util.List;

public interface MessageClassifierRepository {

    List<MessageClassifier> getClassifiers(String brokerName);

    void addClassifier(String brokerName, MessageClassifier messageClassifier);

    void removeAll();
}