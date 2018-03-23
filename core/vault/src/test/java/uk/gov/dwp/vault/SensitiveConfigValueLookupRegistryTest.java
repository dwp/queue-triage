package uk.gov.dwp.vault;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.gov.dwp.vault.domain.DecryptedValue;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.core.Is.is;

public class SensitiveConfigValueLookupRegistryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void ensureThatIfNoResolutionStrategyForAVaultKeyCanBeFound_thenThrowAnIllegalArgumentException() throws Exception {
        SensitiveConfigValueLookupRegistry registry = new SensitiveConfigValueLookupRegistry(emptyList());

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(is("No relevant strategy could be found to resolve vault path for: bogus/path"));

        registry.retrieveSecret("bogus/path");
    }

    @Test
    public void ensureThatIfResolutionStrategyIsFoundButItReturnsNull_thenThrowAnIllegalArgumentException() throws Exception {
        List<SensitiveConfigValueLookupStrategy> listOfStrategies = new ArrayList<>();
        listOfStrategies.add(new SensitiveConfigValueLookupStrategy() {

            @Override
            public DecryptedValue retrieveSecret(String secret) {
                return null;
            }

            @Override
            public boolean matches(String secret) {
                return true;
            }
        });

        SensitiveConfigValueLookupRegistry registry = new SensitiveConfigValueLookupRegistry(listOfStrategies);

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(is("No relevant strategy could be found to resolve vault path for: bogus/path"));

        registry.retrieveSecret("bogus/path");
    }
}