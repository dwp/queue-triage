package uk.gov.dwp.queue.triage.core.stub.app.configuration;

import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@Component
@Import({
        StubAppRepositoryConfiguration.class,
        StubAppJmsConfiguration.class,
        StubAppResourceConfiguration.class
})
public class StubAppConfiguration {
}
