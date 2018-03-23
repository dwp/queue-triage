package uk.gov.dwp.vault.core.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.MapPropertySource;

import uk.gov.dwp.vault.SensitiveConfigValueLookupRegistry;

import java.util.Map;

/**
 * A PropertySource that will hold all the keys which have been defined in config which require vault resolution.
 */
public class VaultPropertySource extends MapPropertySource {

    private static final Logger LOGGER = LoggerFactory.getLogger(VaultPropertySource.class);

    private final SensitiveConfigValueLookupRegistry sensitiveConfigValueLookupRegistry;

    public VaultPropertySource(Map<String, Object> source, SensitiveConfigValueLookupRegistry registry) {
        super("vaultPropertySource", source);
        sensitiveConfigValueLookupRegistry = registry;
    }

    @Override
    public char[] getProperty(String name) {
        if (containsProperty(name)) {
            LOGGER.trace("Attempting to lookup property key = {}", name);
            return sensitiveConfigValueLookupRegistry.retrieveSecret(super.getProperty(name).toString()).getClearText();
        }
        return null;
    }
}
