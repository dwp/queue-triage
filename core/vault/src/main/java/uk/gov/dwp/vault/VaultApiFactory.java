package uk.gov.dwp.vault;

import com.bettercloud.vault.SslConfig;
import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;

import org.springframework.util.ResourceUtils;

import uk.gov.dwp.vault.config.VaultProperties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class VaultApiFactory {

    Vault createVaultAPI(VaultProperties vaultProperties) throws VaultException {
        // The Vault config builder appears to do some caching. If we get a dropped connection or vault is down
        // it does not reconnect. Therefore create a fresh config and vault object for each read.
        return new Vault(buildConfiguration(vaultProperties));
    }

    private VaultConfig buildConfiguration(VaultProperties vaultProperties) throws VaultException {

        VaultConfig vaultConfig = new VaultConfig()
            .address(vaultProperties.getAddress())                                // Defaults to "VAULT_ADDR" environment variable
            .openTimeout(vaultProperties.getOpenTimeout())                        // Defaults to "VAULT_OPEN_TIMEOUT" environment variable
            .readTimeout(vaultProperties.getReadTimeout());                       // Defaults to "VAULT_READ_TIMEOUT" environment variable

        if (vaultProperties.isTokenBaseAuthentication()) {
            VaultProperties.TokenAuthenticationProperties tokenAuthentication = vaultProperties.getTokenAuthentication();
            vaultConfig.token(readTokenFile(tokenAuthentication.getTokenPath()));
        }

        // Only set if specified
        if (vaultProperties.isSslConfigSpecified()) {
            VaultProperties.SslProperties sslProperties = vaultProperties.getSsl();
            vaultConfig.sslConfig(
                new SslConfig()
                    .pemFile(getSslPemFile(sslProperties.getSslPemFilePath()))
                    .verify(sslProperties.getSslVerify())
                    .build());
        }

        return vaultConfig.build();
    }

    private File getSslPemFile(String sslPemFilePath) throws VaultException {
        try {
            return ResourceUtils.getFile(sslPemFilePath);
        } catch (FileNotFoundException e) {
            throw new VaultException(e);
        }
    }

    private String readTokenFile(String path) throws VaultException {
        try {
            byte[] encoded = Files.readAllBytes(ResourceUtils.getFile(path).toPath());
            return new String(encoded, StandardCharsets.UTF_8).trim();
        } catch (IOException e) {
            throw new VaultException(e);
        }
    }
}
