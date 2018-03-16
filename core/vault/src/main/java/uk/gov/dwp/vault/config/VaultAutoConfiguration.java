package uk.gov.dwp.vault.config;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import uk.gov.dwp.queue.triage.secret.lookup.config.QueueTriageApplicationSecretLookupAutoConfiguration;
import uk.gov.dwp.vault.SingleValueVaultLookupStrategy;
import uk.gov.dwp.vault.VaultApiFactory;

@Configuration
@ComponentScan(basePackages = "uk.gov.dwp.vault.config")
// Make sure we actually have something configured.
@ConditionalOnProperty(prefix = "vault", name = "address")
@ImportAutoConfiguration(QueueTriageApplicationSecretLookupAutoConfiguration.class)
@EnableConfigurationProperties(VaultProperties.class)
public class VaultAutoConfiguration {

    @Bean
    public VaultApiFactory vaultApiFactory() {
        return new VaultApiFactory();
    }

    @Bean
    public SingleValueVaultLookupStrategy singleValueVaultLookupStrategy(VaultApiFactory vaultFactoryApi,
                                                                         VaultProperties vaultProperties) {
        return new SingleValueVaultLookupStrategy(vaultProperties, vaultFactoryApi);
    }

}
