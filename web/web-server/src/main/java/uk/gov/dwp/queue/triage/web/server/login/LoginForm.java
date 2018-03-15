package uk.gov.dwp.queue.triage.web.server.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginForm {

    private final String username;
    private final String password;

    public LoginForm(@JsonProperty("username") String username,
                     @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
