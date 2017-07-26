package uk.gov.dwp.queue.triage.web.server.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;

import java.util.Optional;

public class AuthenticationExceptionAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationExceptionAdapter.class);

    public Optional<String> toErrorMessage(AuthenticationException authenticationException) {
        return Optional.ofNullable(authenticationException)
                .map(exception -> {
                    String message = exception.getMessage();
                    LOGGER.debug("Translating error message: {}", message);
                    return "Incorrect username/password";
                });

    }
}
