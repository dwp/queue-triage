package uk.gov.dwp.queue.triage.web.component.login;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import uk.gov.dwp.queue.triage.jgiven.GivenStage;

import java.util.Collections;
import java.util.HashMap;

@JGivenStage
public class LoginApiGivenStage extends GivenStage<LoginApiGivenStage> {

    @Autowired
    private TestRestTemplate testRestTemplate;
    @ExpectedScenarioState
    private HttpHeaders httpHeaders;

    public LoginApiGivenStage theUserHasSuccessfullyLoggedOn() {
            final ResponseEntity<Object> exchange = testRestTemplate.exchange(
                    "/web/login?username={username}&password={password}",
                    HttpMethod.POST,
                    new HttpEntity<>(Collections.singletonMap(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)),
                    new ParameterizedTypeReference<Object>() {},
                    new HashMap<String, String>() {{
                        put("username", UserFixture.USERNAME);
                        put("password", UserFixture.PASSWORD);
                    }}
            );
            httpHeaders = extractCookies(exchange);
            return self();
    }

    private HttpHeaders extractCookies(ResponseEntity<Object> exchange) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        exchange.getHeaders()
                .get(HttpHeaders.SET_COOKIE)
                .forEach(cookie -> httpHeaders.set(HttpHeaders.COOKIE, cookie));
        return httpHeaders;
    }
}
