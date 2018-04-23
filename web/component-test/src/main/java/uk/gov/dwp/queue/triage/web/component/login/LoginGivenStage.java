package uk.gov.dwp.queue.triage.web.component.login;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import static uk.gov.dwp.queue.triage.web.component.login.UserFixture.PASSWORD;
import static uk.gov.dwp.queue.triage.web.component.login.UserFixture.USERNAME;

@JGivenStage
public class LoginGivenStage extends Stage<LoginGivenStage> {

    @Autowired
    private Environment environment;

    public LoginGivenStage theUserHasSuccessfullyLoggedOn() {
        LoginPage.openLoginPage(environment)
                .enterUsername(USERNAME)
                .enterPassword(PASSWORD)
                .submit();
        return this;
    }
}
