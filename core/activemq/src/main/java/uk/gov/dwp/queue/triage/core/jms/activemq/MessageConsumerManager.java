package uk.gov.dwp.queue.triage.core.jms.activemq;

public interface MessageConsumerManager {

    void start();

    void stop();

    boolean isRunning();

    String getBrokerName();
}
