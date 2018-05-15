package uk.gov.dwp.queue.triage.core.domain.update.adapter;

import org.junit.Test;
import org.slf4j.Logger;
import uk.gov.dwp.queue.triage.core.client.update.UpdateRequest;
import uk.gov.dwp.queue.triage.core.domain.FailedMessageBuilder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class LoggingUpdateRequestAdapterTest {

    private final Logger logger = mock(Logger.class);
    private final FailedMessageBuilder failedMessageBuilder = mock(FailedMessageBuilder.class);

    private final LoggingUpdateRequestAdapter underTest = new LoggingUpdateRequestAdapter() {
        @Override
        protected Logger getLogger() {
            return logger;
        }
    };

    @Test
    public void classNameIsLoggedByTheAdapter() {
        underTest.adapt(new ExampleUpdateRequest(), failedMessageBuilder);

        verify(logger).warn("UpdateRequestAdapter not found for: {}", ExampleUpdateRequest.class.getName());
    }

    private static class ExampleUpdateRequest implements UpdateRequest {

    }
}