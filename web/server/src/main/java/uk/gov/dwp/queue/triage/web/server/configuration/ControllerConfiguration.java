package uk.gov.dwp.queue.triage.web.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.migration.mongo.demo.cxf.client.CxfConfiguration;
import uk.gov.dwp.migration.mongo.demo.cxf.client.ResourceRegistry;
import uk.gov.dwp.queue.triage.web.server.home.HomeController;
import uk.gov.dwp.queue.triage.web.server.login.AuthenticationExceptionAdapter;
import uk.gov.dwp.queue.triage.web.server.login.LoginController;

@Configuration
@Import(CxfConfiguration.class)
public class ControllerConfiguration {

    @Bean
    public LoginController loginController(ResourceRegistry resourceRegistry) {
        return resourceRegistry.add(new LoginController(new AuthenticationExceptionAdapter()));
    }

    @Bean
    public HomeController homeController(ResourceRegistry resourceRegistry) {
        return resourceRegistry.add(new HomeController());
    }
}
