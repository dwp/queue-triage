package uk.gov.dwp.vault;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import uk.gov.dwp.vault.config.VaultProperties;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertThat;

public class VaultApiFactoryTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final String FILE_LOCATION_THAT_DOES_NOT_EXIST = "/tmp/file-does-not-exist";
    private static final String VAULT_SERVER_URL = "http://127.0.0.1:8200";
    private VaultApiFactory underTest = new VaultApiFactory();

    @Test
    public void throwsExceptionWhenTokenFileNotFound() throws Exception {
        VaultProperties vaultProperties = createVaultPropertiesWithTokenFileAt(FILE_LOCATION_THAT_DOES_NOT_EXIST);

        expectedException.expect(VaultException.class);
        expectedException.expectCause(isA(NoSuchFileException.class));
        expectedException.expectMessage(is("java.nio.file.NoSuchFileException: /tmp/file-does-not-exist"));

        underTest.createVaultAPI(vaultProperties);

    }

    @Test
    public void readsTokenFileWhenItExists() throws Exception {
        //create a temp token file
        File tokenFile = testFolder.newFile("temp-token");
        String tokenFileLocation = tokenFile.getAbsolutePath();
        Files.write(Paths.get(tokenFileLocation), "TOKEN-VALUE".getBytes());

        VaultProperties vaultProperties = createVaultPropertiesWithTokenFileAt(tokenFileLocation);
        Vault vaultAPI = underTest.createVaultAPI(vaultProperties);

        assertThat(new File(tokenFileLocation).exists(), is(true));
        assertThat(vaultAPI, is(notNullValue()));
    }

    private VaultProperties createVaultPropertiesWithTokenFileAt(String tokenFileLocation) {
        VaultProperties vaultProperties = new VaultProperties();
        vaultProperties.setAddress(VAULT_SERVER_URL);
        vaultProperties.getTokenAuthentication().setTokenPath(tokenFileLocation);
        return vaultProperties;
    }

}