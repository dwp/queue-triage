package uk.gov.dwp.queue.triage.web.component.login;

import com.tngtech.jgiven.integration.spring.EnableJGiven;
import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.dwp.queue.triage.web.component.ComponentTestContext;
import uk.gov.dwp.queue.triage.web.server.QueueTriageWebApplication;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static uk.gov.dwp.queue.triage.web.component.login.LoginStage.User.aUser;

@EnableJGiven
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        classes = {
                QueueTriageWebApplication.class,
                ComponentTestContext.class,
        })
public class LoginComponentTest extends SimpleSpringRuleScenarioTest<LoginStage> {

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
        then().theUserIsRedirectedToTheHomePage();
    }

    @Test
    public void userSubmitsAnIncorrectPassword() {
        given().user$Exists(aUser().withUsername("postmanpat").withPassword("jess"));
        given().theUserIsOnTheLoginPage();
        when().user$AuthenticatesWithPassword$("postmanpat", "topsecret");
        then().theUserRemainsOnTheLoginPageWithTheErrorMessage("Incorrect username/password");
    }
}

