package uk.gov.dwp.queue.triage.core.resend.spring.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.core.resend.ResendScheduledExecutorService;
import uk.gov.dwp.queue.triage.core.resend.ResendScheduledExecutorsResource;
import uk.gov.dwp.queue.triage.cxf.CxfConfiguration;
import uk.gov.dwp.queue.triage.cxf.ResourceRegistry;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Import({
        CxfConfiguration.class
})
public class ResendFailedMessageConfiguration {

    @Bean
    public ResendScheduledExecutorsResource resendScheduledExecutorsResource(ResourceRegistry resourceRegistry,
                                                                             List<ResendScheduledExecutorService> resendScheduledExecutorServices) {
        return resourceRegistry.add(new ResendScheduledExecutorsResource(
                resendScheduledExecutorServices
                        .stream()
                        .collect(Collectors.toMap(ResendScheduledExecutorService::getBrokerName, Function.identity()))));
    }
}
