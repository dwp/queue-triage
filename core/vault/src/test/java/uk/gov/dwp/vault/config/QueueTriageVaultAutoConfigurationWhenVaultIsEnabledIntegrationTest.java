package uk.gov.dwp.vault.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import uk.gov.dwp.queue.triage.secret.lookup.SameValueLookupStrategy;
import uk.gov.dwp.queue.triage.secret.lookup.SensitiveConfigValueLookupRegistry;
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
@EnableConfigurationProperties(VaultProperties.class)
public class QueueTriageVaultAutoConfigurationWhenVaultIsEnabledIntegrationTest {

    @Autowired
    public VaultProperties vaultProperties;

    @Autowired
    public SensitiveConfigValueLookupRegistry sensitiveConfigValueLookupRegistry;

    @Test
    public void ensureThatWhenConfiguringVault_thatTheOrderOfTheServicesAreWhatWeExpect() throws Exception {
        assertNotNull(vaultProperties);
        assertThat(sensitiveConfigValueLookupRegistry.getSortedResolutionStrategies().collect(toList()).size(), is(2));
        assertThat(sensitiveConfigValueLookupRegistry.getSortedResolutionStrategies().collect(toList()).get(0), instanceOf(SingleValueVaultLookupStrategy.class));
        assertThat(sensitiveConfigValueLookupRegistry.getSortedResolutionStrategies().collect(toList()).get(1), instanceOf(SameValueLookupStrategy.class));
    }


}