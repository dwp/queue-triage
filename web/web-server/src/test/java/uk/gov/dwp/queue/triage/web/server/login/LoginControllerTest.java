package uk.gov.dwp.queue.triage.web.server.login;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.web.WebAttributes.AUTHENTICATION_EXCEPTION;

public class LoginControllerTest {

    private final AuthenticationExceptionAdapter authenticationExceptionAdapter = mock(AuthenticationExceptionAdapter.class);

    private final LoginController underTest = new LoginController(authenticationExceptionAdapter);
    private final HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    private final HttpSession httpSession = mock(HttpSession.class);
    private final AuthenticationException authenticationException = mock(AuthenticationException.class);

    @Test
    public void loginPageIsCreatedSuccessfully() throws Exception {
        when(httpServletRequest.getSession()).thenReturn(httpSession);
        when(httpSession.getAttribute(AUTHENTICATION_EXCEPTION)).thenReturn(authenticationException);
        when(authenticationExceptionAdapter.toErrorMessage(authenticationException)).thenReturn(Optional.of("Error Message"));

        assertThat(underTest.getLoginPage(httpServletRequest), hasErrorMessage("Error Message"));
    }

    private TypeSafeMatcher<LoginPage> hasErrorMessage(final String errorMessage) {
        return new TypeSafeMatcher<LoginPage>() {
            @Override
            protected boolean matchesSafely(LoginPage loginPage) {
                return errorMessage.equals(loginPage.errorMessage());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("errorMessage is 'Error Message'");
            }
        };
    }
}