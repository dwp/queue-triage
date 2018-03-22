package uk.gov.dwp.queue.triage.core.jms.activemq;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MessageConsumerManagerRegistry {

    private final Map<String, MessageConsumerManager> messageConsumerManagers;

    public MessageConsumerManagerRegistry() {
        this(new HashMap<>());
    }

    public MessageConsumerManagerRegistry(Map<String, MessageConsumerManager> messageConsumerManagers) {
        this.messageConsumerManagers = messageConsumerManagers;
    }

    public MessageConsumerManagerRegistry with(String name, MessageConsumerManager messageConsumerManager) {
        add(name, messageConsumerManager);
        return this;
    }

    public MessageConsumerManager add(String name, MessageConsumerManager messageConsumerManager) {
        messageConsumerManagers.put(name, messageConsumerManager);
        return messageConsumerManager;
    }

    public Optional<MessageConsumerManager> get(String name) {
        return Optional.ofNullable(messageConsumerManagers.get(name));
    }
}
