package uk.gov.dwp.vault;

import uk.gov.dwp.vault.domain.DecryptedValue;

public interface SensitiveConfigValueLookupStrategy {

    DecryptedValue retrieveSecret(String secret);

    boolean matches(String secret);

}
