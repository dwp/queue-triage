package uk.gov.dwp.queue.triage.core.stub.app.repository;

import uk.gov.dwp.queue.triage.core.classification.MessageClassifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryMessageClassifierRepository implements MessageClassifierRepository {

    private final Map<String, List<MessageClassifier>> messageClassifiers = new ConcurrentHashMap<>();

    @Override
    public List<MessageClassifier> getClassifiers(String brokerName) {
        return new ArrayList<>(Optional.ofNullable(messageClassifiers.get(brokerName)).orElse(new ArrayList<>()));
    }

    @Override
    public void addClassifier(String brokerName, MessageClassifier messageClassifier) {
        if (!messageClassifiers.containsKey(brokerName)) {
            messageClassifiers.put(brokerName, Collections.synchronizedList(new ArrayList<>()));
        }
        messageClassifiers.get(brokerName).add(messageClassifier);
    }

    @Override
    public void removeAll() {
        messageClassifiers.clear();
    }
}
