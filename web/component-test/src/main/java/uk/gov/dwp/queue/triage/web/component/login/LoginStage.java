package uk.gov.dwp.queue.triage.web.component.login;

import com.google.common.collect.ImmutableMap;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.queue.triage.jgiven.ReflectionArgumentFormatter;
import uk.gov.dwp.queue.triage.web.component.home.HomePage;
import uk.gov.dwp.queue.triage.web.component.list.ListFailedMessagesPage;
import uk.gov.dwp.queue.triage.web.component.login.LoginStage.User.UserBuilder;
import uk.gov.dwp.queue.triage.web.component.util.PageSupport;

import java.util.HashMap;
import java.util.Map;

@JGivenStage
public class LoginStage extends Stage<LoginStage> {

    @Autowired
    private Environment environment;

    @ExpectedScenarioState
    private Map<String, User> users = new HashMap<>();

    @ExpectedScenarioState
    private ResponseEntity response;

    @ExpectedScenarioState
    private LoginPage loginPage;

    @ExpectedScenarioState
    private HomePage homePage;

    // Given
    public LoginStage user$Exists(@Format(value = ReflectionArgumentFormatter.class, args = {"username", "password"}) UserBuilder userBuilder) {
        final User user = userBuilder.build();
        users.put(user.getUsername(), user);
        // Check if the user exists
        // Create if required
        return this;
    }

    // When
    public LoginStage user$AuthenticatesWithPassword$(String username, String password) {
        loginPage
                .enterUsername(username)
                .enterPassword(password)
                .submit();
        return this;

    }

    // When
    public LoginStage aRequestIsMadeTo(String url) {
        loginPage = PageSupport.navigateTo(environment, LoginPage.class, url);
        return this;
    }

    // Given
    public LoginStage theUserIsOnTheLoginPage() {
        loginPage = LoginPage.openLoginPage(environment);
        return this;
    }

    // Then
    public LoginStage theUserIsRedirectedToTheLoginPage() {
        LoginPage.ensureLoaded();
        return this;
    }

    public LoginStage theUserIsUnauthenticated() {
        PageSupport.navigateTo(environment, LoginPage.class, "logout");
        return this;
    }

    public LoginStage theUserIsRedirectedToTheFailedMessageListPage() {
        ListFailedMessagesPage.ensureLoaded();
        return this;
    }

    public LoginStage theUserRemainsOnTheLoginPageWithTheErrorMessage(String errorMessage) {
        LoginPage
                .ensureLoaded()
                .hasErrorMessage(errorMessage);
        return this;
    }

    public static class User {
        private final String username;
        private final String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public static UserBuilder aUser() {
            return new UserBuilder();
        }

        public Map<String, String> toMap() {
            return ImmutableMap.of(
                    "username", username,
                    "password", password
            );
        }

        public static class UserBuilder {
            private String username;
            private String password = "t0p53cr3t";

            private UserBuilder() {
            }

            public UserBuilder withUsername(String username) {
                this.username = username;
                return this;
            }

            public UserBuilder withPassword(String password) {
                this.password = password;
                return this;
            }

            public User build() {
                return new User(username, password);
            }
        }
    }
}
