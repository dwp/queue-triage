package uk.gov.dwp.vault;

import org.junit.Test;

import uk.gov.dwp.vault.domain.DecryptedValue;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SameValueLookupStrategyTest {

    private static final String PATH = "passedThrough";
    private SameValueLookupStrategy underTest = new SameValueLookupStrategy();

    @Test
    public void matchesIsAlwaysTrue() throws Exception {
        assertThat(underTest.matches("anything"), is(true));
        assertThat(underTest.matches(null), is(true));
    }

    @Test
    public void passesThrough() throws Exception {
        DecryptedValue secret = underTest.retrieveSecret(PATH);
        assertThat(secret.getClearText(), is(PATH.toCharArray()));
    }

}