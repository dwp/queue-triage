package uk.gov.dwp.vault;


import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;

import uk.gov.dwp.queue.triage.secret.lookup.SensitiveConfigValueLookupStrategy;
import uk.gov.dwp.vault.config.VaultProperties;
import uk.gov.dwp.queue.triage.secret.lookup.domain.DecryptedValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This assumes that values looked up in vault are basically in the format of "<VALUE_KEY>=<SECRET_VALUE>". Vault is capable of storing an object as the secret, but
 * this strategy is only concerned with single field values defined by the key called "value"
 */
public class SingleValueVaultLookupStrategy implements SensitiveConfigValueLookupStrategy {

    private static final String VALUE_KEY = "value";
    private static final Pattern PATTERN = Pattern.compile("VAULT\\((.+)\\)");

    private final VaultProperties vaultProperties;
    private final VaultApiFactory vaultApiFactory;


    public SingleValueVaultLookupStrategy(VaultProperties vaultProperties, VaultApiFactory vaultApiFactory) {
        this.vaultProperties = vaultProperties;
        this.vaultApiFactory = vaultApiFactory;
    }

    @Override
    public boolean matches(String secret) {
        return isVaultEnabledAndPatternMatches(secret);
    }

    @Override
    public int evaluationOrder() {
        return FIRST_EVAL_ORDER;
    }

    @Override
    public DecryptedValue retrieveSecret(String secretPath) {
        Matcher matcher = PATTERN.matcher(secretPath);
        if (matcher.matches()) {
            String vaultPath = matcher.group(1);
            try {
                String value = vaultAPI().logical().read(vaultPath).getData().get(VALUE_KEY);
                return new DecryptedValue(value.toCharArray());
            } catch (VaultException e) {
                int httpStatusCode = e.getHttpStatusCode();
                switch (httpStatusCode) {
                    case 0:
                        throw new RuntimeException("Unable to connect to vault at:'" + vaultProperties.getAddress() + "', looking up the path '" + secretPath + "'", e);
                    case 404: // Fallthrough
                    case 403:
                        throw new RuntimeException("Connected to '" + vaultProperties.getAddress() + "': unable to read secret at path '" + secretPath + "'", e);
                    default:
                        throw new RuntimeException("HTTP status " + httpStatusCode + ". Could not retrieve from Vault '" + vaultProperties.getAddress() + "': the path '" + secretPath + "'", e);
                }
            }
        } else {
            // Should never get here since we expect matches to have been called to select this service.
            throw new IllegalStateException("Secret: '" + secretPath + "' did not match pattern did you call matches(secret) first?");
        }
    }

    private boolean isVaultEnabledAndPatternMatches(String configPath) {
        return PATTERN.matcher(configPath).matches();
    }


    private Vault vaultAPI() throws VaultException {
        return vaultApiFactory.createVaultAPI(vaultProperties);
    }

}
