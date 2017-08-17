package uk.gov.dwp.queue.triage.web.component.login;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@JGivenStage
public class LoginGivenStage extends Stage<LoginGivenStage> {

    @Autowired
    private Environment environment;

    public LoginGivenStage theUserHasSuccessfullyLoggedOn() {
        LoginPage.openLoginPage(environment)
                .enterUsername("bobbuilder")
                .enterPassword("fixit")
                .submit();
        return this;
    }
}
