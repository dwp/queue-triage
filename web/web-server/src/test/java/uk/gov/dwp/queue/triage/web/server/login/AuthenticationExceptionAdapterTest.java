package uk.gov.dwp.queue.triage.web.server.login;

import org.junit.Test;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class AuthenticationExceptionAdapterTest {

    private final AuthenticationExceptionAdapter underTest = new AuthenticationExceptionAdapter();

    @Test
    public void nullExceptionReturnsEmptyString() throws Exception {
        assertThat(underTest.toErrorMessage(null), is(Optional.empty()));
    }

    @Test
    public void anyAuthenticationExceptionReturns() {
        assertThat(underTest.toErrorMessage(mock(AuthenticationException.class)), is(Optional.of("Incorrect username/password")));
    }

}