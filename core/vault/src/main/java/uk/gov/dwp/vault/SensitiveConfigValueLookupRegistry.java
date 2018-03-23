package uk.gov.dwp.vault;

import uk.gov.dwp.vault.domain.DecryptedValue;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class SensitiveConfigValueLookupRegistry {

    private final List<SensitiveConfigValueLookupStrategy> resolutionStrategies;

    public SensitiveConfigValueLookupRegistry(List<SensitiveConfigValueLookupStrategy> resolutionStrategies) {
        this.resolutionStrategies = resolutionStrategies;
    }

    public DecryptedValue retrieveSecret(String secretPath) {
        return resolutionStrategies
            .stream()
            .filter(service -> service.matches(secretPath))
            .findFirst()
            .map(service -> service.retrieveSecret(secretPath))
            .orElseThrow(() -> new IllegalArgumentException("No relevant strategy could be found to resolve vault path for: " + secretPath));
    }
}
