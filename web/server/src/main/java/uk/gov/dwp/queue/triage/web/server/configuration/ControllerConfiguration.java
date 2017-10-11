package uk.gov.dwp.queue.triage.web.server.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.core.client.delete.DeleteFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.resend.ResendFailedMessageClient;
import uk.gov.dwp.queue.triage.cxf.CxfConfiguration;
import uk.gov.dwp.queue.triage.cxf.ResourceRegistry;
import uk.gov.dwp.queue.triage.core.client.SearchFailedMessageClient;
import uk.gov.dwp.queue.triage.core.client.label.LabelFailedMessageClient;
import uk.gov.dwp.queue.triage.jackson.configuration.JacksonConfiguration;
import uk.gov.dwp.queue.triage.web.server.api.FailedMessageChangeResource;
import uk.gov.dwp.queue.triage.web.server.api.resend.ResendFailedMessageResource;
import uk.gov.dwp.queue.triage.web.server.home.HomeController;
import uk.gov.dwp.queue.triage.web.server.list.FailedMessageListController;
import uk.gov.dwp.queue.triage.web.server.list.FailedMessageListItemAdapter;
import uk.gov.dwp.queue.triage.web.server.api.LabelExtractor;
import uk.gov.dwp.queue.triage.web.server.login.AuthenticationExceptionAdapter;
import uk.gov.dwp.queue.triage.web.server.login.LoginController;

@Configuration
@Import({
        CxfConfiguration.class,
        CoreClientConfiguration.class,
        JacksonConfiguration.class
})
public class ControllerConfiguration {

    @Bean
    public LoginController loginController(ResourceRegistry resourceRegistry) {
        return resourceRegistry.add(new LoginController(new AuthenticationExceptionAdapter()));
    }

    @Bean
    public HomeController homeController(ResourceRegistry resourceRegistry) {
        return resourceRegistry.add(new HomeController());
    }

    @Bean
    public FailedMessageListController failedMessageListController(ResourceRegistry resourceRegistry,
                                                                   SearchFailedMessageClient searchFailedMessageClient) {
        return resourceRegistry.add(new FailedMessageListController(
                searchFailedMessageClient,
                new FailedMessageListItemAdapter()
        ));
    }

    @Bean
    public FailedMessageChangeResource failedMessageChangeResource(ResourceRegistry resourceRegistry,
                                                                   LabelFailedMessageClient labelFailedMessageClient,
                                                                   DeleteFailedMessageClient deleteFailedMessageClient) {
        return resourceRegistry.add(new FailedMessageChangeResource(
                labelFailedMessageClient,
                new LabelExtractor(),
                deleteFailedMessageClient
        ));
    }

    @Bean
    public ResendFailedMessageResource resendFailedMessageResource(ResourceRegistry resourceRegistry,
                                                            ResendFailedMessageClient resendFailedMessageClient) {
        return resourceRegistry.add(new ResendFailedMessageResource(resendFailedMessageClient));
    }
}
