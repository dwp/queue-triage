package uk.gov.dwp.vault;


import uk.gov.dwp.vault.domain.DecryptedValue;

/**
 * This lookup strategy just returns the same values that was supplied to it. Serves as a good fallback, when vault is not enabled.
 */
public class SameValueLookupStrategy implements SensitiveConfigValueLookupStrategy {

    @Override
    public DecryptedValue retrieveSecret(String secret) {
        return new DecryptedValue(secret.toCharArray());
    }

    @Override
    public boolean matches(String secret) {
        return true;
    }
}
