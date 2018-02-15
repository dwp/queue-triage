package uk.gov.dwp.queue.triage.executor;

public interface BrokerExecutorService {

    void start();

    void execute();

    void pause();

    void shutdown();

    String getBrokerName();

    boolean isRunning();
}
