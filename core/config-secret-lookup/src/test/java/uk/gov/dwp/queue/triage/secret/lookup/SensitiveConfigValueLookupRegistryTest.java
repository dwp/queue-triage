package uk.gov.dwp.queue.triage.secret.lookup;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.gov.dwp.queue.triage.secret.lookup.domain.DecryptedValue;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class SensitiveConfigValueLookupRegistryTest {

    private static final String SECRET = "BobbyP";
    private static final DecryptedValue DECRYPTED_PASSWORD = new DecryptedValue(SECRET.toCharArray());
    private SensitiveConfigValueLookupStrategy resolutionStrategy = mock(SensitiveConfigValueLookupStrategy.class);
    private SensitiveConfigValueLookupRegistry underTest = new SensitiveConfigValueLookupRegistry(singletonList(resolutionStrategy));

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void configuredSensitiveConfigValueDelegatingServiceMatches() throws Exception {
        when(resolutionStrategy.matches(SECRET)).thenReturn(true);
        when(resolutionStrategy.retrieveSecret(SECRET)).thenReturn(DECRYPTED_PASSWORD);

        DecryptedValue result = underTest.retrieveSecret(SECRET);
        assertThat(result, is(DECRYPTED_PASSWORD));
    }

    @Test
    public void noSensitiveConfigValueDelegatingServiceMatches() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(is("No Decryption Service registered for: BobbyP"));

        underTest.retrieveSecret(SECRET);
    }
}
