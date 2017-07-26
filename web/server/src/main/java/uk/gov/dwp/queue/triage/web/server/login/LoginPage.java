package uk.gov.dwp.queue.triage.web.server.login;

import uk.gov.dwp.queue.triage.web.common.Page;

import java.util.Optional;

public class LoginPage extends Page {

    private Optional<String> errorMessage;

    public LoginPage(Optional<String> errorMessage) {
        super("login.mustache");
        this.errorMessage = errorMessage;
    }

    public boolean isErrorPresent() {
        return errorMessage.isPresent();
    }

    public String errorMessage() {
        return errorMessage.orElse("");
    }
}
