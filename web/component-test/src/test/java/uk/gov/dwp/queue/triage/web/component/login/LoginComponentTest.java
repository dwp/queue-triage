package uk.gov.dwp.queue.triage.web.component.login;

import org.junit.Test;
import uk.gov.dwp.queue.triage.web.component.BaseWebComponentTest;

import static uk.gov.dwp.queue.triage.web.component.login.LoginStage.User.aUser;

public class LoginComponentTest extends BaseWebComponentTest<LoginStage> {

    @Test
    public void userAttemptsToAccessASecureArea() throws Exception {
        given().theUserIsUnauthenticated();
        when().aRequestIsMadeTo("/secure");
        then().theUserIsRedirectedToTheLoginPage();
    }

    @Test
    public void unUnknownUserAttemptsToAuthenticate() {
        given().theUserIsOnTheLoginPage();
        when().user$AuthenticatesWithPassword$("noone", "topsecret");
        then().theUserRemainsOnTheLoginPageWithTheErrorMessage("Incorrect username/password");
    }

    @Test
    public void userSuccessfullyAuthenticates() {
        given().user$Exists(aUser().withUsername("postmanpat").withPassword("jess"));
        given().theUserIsOnTheLoginPage();
        when().user$AuthenticatesWithPassword$("postmanpat", "jess");
        then().theUserIsRedirectedToTheFailedMessageListPage();
    }

    @Test
    public void userSubmitsAnIncorrectPassword() {
        given().user$Exists(aUser().withUsername("postmanpat").withPassword("jess"));
        given().theUserIsOnTheLoginPage();
        when().user$AuthenticatesWithPassword$("postmanpat", "topsecret");
        then().theUserRemainsOnTheLoginPageWithTheErrorMessage("Incorrect username/password");
    }
}

