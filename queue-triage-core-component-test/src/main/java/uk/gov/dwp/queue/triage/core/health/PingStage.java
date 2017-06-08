package uk.gov.dwp.queue.triage.core.health;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@JGivenStage
public class PingStage extends Stage<PingStage> {

    @Autowired
    private TestRestTemplate restTemplate;

    private String response;

    PingStage aRequestIsMadeToPing() throws IOException {
        response = restTemplate.getForEntity("/ping", String.class).getBody();
        return this;
    }

    PingStage theResponseIs(String expectedResponse) {
        assertThat(response, is(expectedResponse));
        return this;
    }
}