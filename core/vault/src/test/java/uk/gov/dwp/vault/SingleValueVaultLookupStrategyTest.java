package uk.gov.dwp.vault;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.api.Logical;
import com.bettercloud.vault.response.LogicalResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.gov.dwp.queue.triage.secret.lookup.domain.DecryptedValue;
import uk.gov.dwp.vault.config.VaultProperties;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SingleValueVaultLookupStrategyTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String SOME_PATH = "/some/secret/path";
    private static final String MATCHING_PATH = "VAULT(" + SOME_PATH + ")";
    private static final String NON_MATCHING_PATH = "not-a-vault-path";
    private static final VaultProperties VAULT_PROPERTIES = new VaultProperties();
    private static final String SECRET_VALUE = "TestingSecretValue";
    private static final String EXPECTED_VALUE_KEY = "value";

    private VaultApiFactory factory = mock(VaultApiFactory.class);
    private Vault vault = mock(Vault.class);

    private SingleValueVaultLookupStrategy underTest = new SingleValueVaultLookupStrategy(VAULT_PROPERTIES, factory);

    @Before
    public void setUp() throws Exception {
        when(factory.createVaultAPI(VAULT_PROPERTIES)).thenReturn(vault);
    }

    @Before
    public void fakeSuccessfulResponseFromVault() throws Exception {
        Logical logical = mock(Logical.class);
        LogicalResponse logicalResponse = mock(LogicalResponse.class);
        Map<String, String> expectedResponseData = new HashMap<>();
        expectedResponseData.put(EXPECTED_VALUE_KEY, SECRET_VALUE);

        when(vault.logical()).thenReturn(logical);
        when(logical.read(SOME_PATH)).thenReturn(logicalResponse);
        when(logicalResponse.getData()).thenReturn(expectedResponseData);
    }


    @Test
    public void matchesWhenConfiguredAndCorrectPath() throws Exception {
        assertThat(underTest.matches(MATCHING_PATH), is(true));
    }

    @Test
    public void secretPathDoesNotMatch() throws Exception {
        assertThat(underTest.matches(NON_MATCHING_PATH), is(false));
    }

    @Test
    public void unableToRetrieveIfNotAVaultSecretPath() throws Exception {
        expectedException.expect(IllegalStateException.class);
        underTest.retrieveSecret(NON_MATCHING_PATH);
    }

    @Test
    public void readsSecretFromVault() throws Exception {
        DecryptedValue decryptedPassword = underTest.retrieveSecret(MATCHING_PATH);

        assertThat(decryptedPassword.getClearText(), is(SECRET_VALUE.toCharArray()));
    }

    @Test
    public void wrapsVaultException() throws Exception {
        doAnswer(invocation -> {
            throw new VaultException("expected");
        }).when(vault).logical();

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Unable to connect to vault at:'null', looking up the path 'VAULT(/some/secret/path)'");

        underTest.retrieveSecret(MATCHING_PATH);
    }

    @Test
    public void ensureThatARuntimeExceptionIsThrownWhenVaultKeyIsNotFound() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(is("Connected to '" + VAULT_PROPERTIES.getAddress() + "': unable to read secret at path '" + MATCHING_PATH + "'"));

        Logical logical = mock(Logical.class);
        when(vault.logical()).thenReturn(logical);
        when(logical.read(SOME_PATH)).thenThrow(new VaultException("Oops", 404));

        underTest.retrieveSecret(MATCHING_PATH);
    }
}