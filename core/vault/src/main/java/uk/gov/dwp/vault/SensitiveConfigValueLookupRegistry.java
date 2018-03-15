package uk.gov.dwp.vault;


import uk.gov.dwp.vault.domain.DecryptedValue;

import java.util.List;

public class SensitiveConfigValueLookupRegistry {

    public final List<SensitiveConfigValueLookupStrategy> resolutionStrategies;

    public SensitiveConfigValueLookupRegistry(List<SensitiveConfigValueLookupStrategy> resolutionStrategies) {
        this.resolutionStrategies = resolutionStrategies;
    }

    public DecryptedValue retrieveSecret(String secretPath) {
        return resolutionStrategies
            .stream()
            .filter(service -> service.matches(secretPath))
            .findFirst()
            .map(service -> service.retrieveSecret(secretPath))
            .orElseThrow(() -> new IllegalArgumentException("No Decryption Service registered for: " + secretPath));
    }
}
