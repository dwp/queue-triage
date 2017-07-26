package uk.gov.dwp.queue.triage.web.server.login;

import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.springframework.security.web.WebAttributes.AUTHENTICATION_EXCEPTION;

@Path("/login")
@Produces(TEXT_HTML)
public class LoginController {

    private final AuthenticationExceptionAdapter authenticationExceptionAdapter;

    public LoginController(AuthenticationExceptionAdapter authenticationExceptionAdapter) {
        this.authenticationExceptionAdapter = authenticationExceptionAdapter;
    }

    @GET
    public LoginPage getLoginPage(@Context HttpServletRequest request) {
        Optional<String> errorMessage = authenticationExceptionAdapter
                .toErrorMessage((AuthenticationException) request.getSession().getAttribute(AUTHENTICATION_EXCEPTION));
        return new LoginPage(errorMessage);
    }
}
