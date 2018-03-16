package uk.gov.dwp.queue.triage.secret.lookup;


import uk.gov.dwp.queue.triage.secret.lookup.domain.DecryptedValue;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class SensitiveConfigValueLookupRegistry {

    private final List<SensitiveConfigValueLookupStrategy> resolutionStrategies;

    public SensitiveConfigValueLookupRegistry(List<SensitiveConfigValueLookupStrategy> resolutionStrategies) {
        this.resolutionStrategies = resolutionStrategies;
    }

    public DecryptedValue retrieveSecret(String secretPath) {
        return getSortedResolutionStrategies()
            .filter(service -> service.matches(secretPath))
            .findFirst()
            .map(service -> service.retrieveSecret(secretPath))
            .orElseThrow(() -> new IllegalArgumentException("No Decryption Service registered for: " + secretPath));
    }

    public Stream<SensitiveConfigValueLookupStrategy> getSortedResolutionStrategies() {
        return resolutionStrategies
            .stream()
            .sorted(Comparator.comparing(SensitiveConfigValueLookupStrategy::evaluationOrder));
    }
}
