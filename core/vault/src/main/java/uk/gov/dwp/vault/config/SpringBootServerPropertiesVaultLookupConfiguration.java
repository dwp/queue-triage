package uk.gov.dwp.vault.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.Ssl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import uk.gov.dwp.queue.triage.secret.lookup.SensitiveConfigValueLookupRegistry;
import uk.gov.dwp.vault.VaultApiFactory;

import java.util.Optional;

/**
 * This class is mainly responsible for intercepting the ServerProperties defined in the yaml config and looking them up in Vault before the #{{@link
 * org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainer}} is started to avoid exceptions being thrown at container startup because the keystore password is "wrong", but in
 * fact it's still the vault secret path, and not the resolved value.
 */
@Configuration
@ConditionalOnBean(value = VaultApiFactory.class)
public class SpringBootServerPropertiesVaultLookupConfiguration implements EmbeddedServletContainerCustomizer, Ordered, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootServerPropertiesVaultLookupConfiguration.class);

    private ApplicationContext applicationContext;

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        ServerProperties serverProperties = applicationContext.getBean(ServerProperties.class);
        SensitiveConfigValueLookupRegistry sensitiveConfigValueDelegatingService = applicationContext.getBean(SensitiveConfigValueLookupRegistry.class);

        Optional<Ssl> optionalSslProperties = Optional.ofNullable(serverProperties.getSsl());
        optionalSslProperties.ifPresent(sslProperties -> {

            LOGGER.info("Running https server under. Attempting to resolve the server.ssl.* yaml config via Vault before server startup.");

            String resolvedKeyStorePassword = resolveOrReturnOriginalValue(sensitiveConfigValueDelegatingService, sslProperties.getKeyStorePassword());
            String resolvedTrustStorePassword = resolveOrReturnOriginalValue(sensitiveConfigValueDelegatingService, sslProperties.getTrustStorePassword());
            String resolvedKeyPassword = resolveOrReturnOriginalValue(sensitiveConfigValueDelegatingService, sslProperties.getKeyPassword());

            sslProperties.setKeyStorePassword(resolvedKeyStorePassword);
            sslProperties.setTrustStorePassword(resolvedTrustStorePassword);
            sslProperties.setKeyPassword(resolvedKeyPassword);

            serverProperties.setSsl(sslProperties);
        });
    }

    private String resolveOrReturnOriginalValue(SensitiveConfigValueLookupRegistry sensitiveConfigValueDelegatingService, String vaultPath) {
        return Optional.ofNullable(vaultPath).map(item -> resolveVaultSecretPath(sensitiveConfigValueDelegatingService, item)).orElse(vaultPath);
    }

    private String resolveVaultSecretPath(SensitiveConfigValueLookupRegistry sensitiveConfigValueDelegatingService, String vaultSecretPath) {
        return String.valueOf(sensitiveConfigValueDelegatingService.retrieveSecret(vaultSecretPath).getClearText());
    }

}
