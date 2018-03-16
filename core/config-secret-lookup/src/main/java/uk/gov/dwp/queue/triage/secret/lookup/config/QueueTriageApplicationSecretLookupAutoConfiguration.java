package uk.gov.dwp.queue.triage.secret.lookup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import uk.gov.dwp.queue.triage.secret.lookup.SameValueLookupStrategy;
import uk.gov.dwp.queue.triage.secret.lookup.SensitiveConfigValueLookupRegistry;
import uk.gov.dwp.queue.triage.secret.lookup.SensitiveConfigValueLookupStrategy;

import java.util.List;

/**
 * This configuration class is useful for providing a mechanism to wire in password/secret value resolution strategies.
 */
@Configuration
@ComponentScan(basePackages = "uk.gov.dwp.queue.triage.secret.lookup")
public class QueueTriageApplicationSecretLookupAutoConfiguration {

    @Bean
    //Always return a lookup strategy that just returns the same value in the config for cases where you do not require any vault setup.
    public SameValueLookupStrategy sameValueLookupStrategy() {
        return new SameValueLookupStrategy();
    }

    // This is a convenience method to wire in *any* instances of the SensitiveConfigValueLookupStrategy
    @Bean
    public SensitiveConfigValueLookupRegistry sensitiveConfigValueDelegatingService(List<SensitiveConfigValueLookupStrategy> strategies) {
        return new SensitiveConfigValueLookupRegistry(strategies);
    }


}
