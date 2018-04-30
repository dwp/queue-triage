package uk.gov.dwp.vault.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import uk.gov.dwp.vault.SensitiveConfigValueLookupRegistry;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = {
        VaultTestSpringBootApplication.class,
    })
@ActiveProfiles(value = "vault-enabled")
@EnableAutoConfiguration
public class QueueTriageVaultAutoConfigurationWhenVaultIsEnabledIntegrationTest {

    @Autowired
    public VaultProperties vaultProperties;

    @Autowired
    public SensitiveConfigValueLookupRegistry sensitiveConfigValueLookupRegistry;

    @Autowired
    public ConversionService conversionService;

    @Test
    public void ensureThatWhenConfiguringVault_thatTheOrderOfTheServicesAreWhatWeExpect() throws Exception {
        assertNotNull(vaultProperties);

        assertThat(vaultProperties.getAddress(), is("http://localhost:8989/vaulty-vaultster"));
        assertThat(vaultProperties.getOpenTimeout(), is(20));
        assertThat(vaultProperties.getReadTimeout(), is(25));
        assertThat(vaultProperties.getTokenAuthentication().getTokenPath(), is("/var/lib/tokenz"));
        assertThat(vaultProperties.getSsl().getSslPemFilePath(), is("/var/pem/file"));
        assertThat(vaultProperties.getSsl().getSslVerify(), is(true));

        assertNotNull(sensitiveConfigValueLookupRegistry);
    }

    @Test
    public void ensureConversionServiceHasAConverterRegisteredToConvertFromCharArrayToString() throws Exception {
        assertNotNull("There needs to be a registered ConversionService bean instance", conversionService);

        assertThat("There is no converter registered which can convert from char[] to String. "
                   + "This is necessary for the implementation of the VaultPropertySource, which returns a char[] for storing "
                   + "passwords in ConfigurationProperties POJO's. This is mainly used for instances where Vault resolved values "
                   + "need to be converted into String for POJO's that we have no control over. For an example, "
                   + "see the org.springframework.boot.context.embedded.Ssl.java which stores the keyStorePassword field as a String.",
                   conversionService.canConvert(char[].class, String.class),
                   is(true));
    }
}