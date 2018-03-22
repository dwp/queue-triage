package uk.gov.dwp.vault.core.env;

import org.junit.Test;

import uk.gov.dwp.vault.SensitiveConfigValueLookupRegistry;
import uk.gov.dwp.vault.domain.DecryptedValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class VaultPropertySourceTest {

    private SensitiveConfigValueLookupRegistry registry = mock(SensitiveConfigValueLookupRegistry.class);

    @Test
    public void ensureThatPropertyKeyThatRequiresResolutionFromVault_isDelegatedToTheUnderlyingRegistry() throws Exception {
        //arrange
        Map<String, Object> mapOfKeys = new HashMap<>();
        String expectedTestKey = "expected.test.key";
        String vaultConfigValue = "VAULT(my/secret/path)";
        char[] expectedResolvedValue = new char[]{'p', 'a', 's', 's'};

        mapOfKeys.put(expectedTestKey, vaultConfigValue);
        when(registry.retrieveSecret(vaultConfigValue)).thenReturn(new DecryptedValue(expectedResolvedValue));

        VaultPropertySource vaultPropertySource = new VaultPropertySource(mapOfKeys, registry);
        //act
        char[] actualResolverKey = vaultPropertySource.getProperty(expectedTestKey);

        //assert
        assertTrue(Arrays.equals(actualResolverKey, expectedResolvedValue));
    }

    @Test
    public void ensureThatIfAttemptingToRetriveAKeyThatIsNotInTheListOfResovableKeys_thenReturnNull() throws Exception {
        VaultPropertySource vaultPropertySource = new VaultPropertySource(new HashMap<>(), registry);
        char[] property = vaultPropertySource.getProperty("non-existent-key");

        assertNull(property);
        verifyZeroInteractions(registry);
    }
}