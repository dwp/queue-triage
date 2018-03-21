package uk.gov.dwp.vault.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import uk.gov.dwp.vault.SensitiveConfigValueLookupRegistry;
import uk.gov.dwp.vault.SingleValueVaultLookupStrategy;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
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

    @Test
    public void ensureThatWhenConfiguringVault_thatTheOrderOfTheServicesAreWhatWeExpect() throws Exception {
        assertNotNull(vaultProperties);

        assertThat(vaultProperties.getAddress(), is("http://localhost:8989/vaulty-vaultster"));
        assertThat(vaultProperties.getOpenTimeout(), is(20));
        assertThat(vaultProperties.getReadTimeout(), is(25));
        assertThat(vaultProperties.getTokenAuthentication().getTokenPath(), is("/var/lib/tokenz"));
        assertThat(vaultProperties.getSsl().getSslPemFilePath(), is("/var/pem/file"));
        assertThat(vaultProperties.getSsl().getSslVerify(), is(true));


        assertThat(sensitiveConfigValueLookupRegistry.getSortedResolutionStrategies().collect(toList()).size(), is(1));
        assertThat(sensitiveConfigValueLookupRegistry.getSortedResolutionStrategies().collect(toList()).get(0), instanceOf(SingleValueVaultLookupStrategy.class));
    }


}