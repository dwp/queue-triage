package uk.gov.dwp.queue.triage.web.component.login;

import com.codeborne.selenide.Condition;
import org.openqa.selenium.By;
import org.springframework.core.env.Environment;
import uk.gov.dwp.queue.triage.web.component.home.HomePage;
import uk.gov.dwp.queue.triage.web.component.util.PageSupport;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

public class LoginPage {

    public static LoginPage openLoginPage(Environment environment) {
        return PageSupport.navigateTo(environment, LoginPage.class, "login");
    }

    public LoginPage enterUsername(String username) {
        $(By.name("username")).setValue(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        $(By.name("password")).setValue(password);
        return this;
    }

    public HomePage submit() {
        $(By.id("btn-login")).click();
        return page(HomePage.class);
    }

    public LoginPage hasErrorMessage(String errorMessage) {
        $(By.id("error-message")).shouldHave(Condition.text(errorMessage));
        return this;
    }

    public static LoginPage ensureLoaded() {
        $(By.id("queue-triage-login")).should(Condition.exist);
        return new LoginPage();
    }
}
