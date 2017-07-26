package uk.gov.dwp.queue.triage.web.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.migration.mongo.demo.cxf.configuration.CxfBusConfiguration;
import uk.gov.dwp.queue.triage.web.common.mustache.configuration.MustacheConfiguration;
import uk.gov.dwp.queue.triage.web.security.spring.ldap.LdapSecurityConfig;
import uk.gov.dwp.queue.triage.web.security.spring.web.WebSecurityConfig;
import uk.gov.dwp.queue.triage.web.server.configuration.ControllerConfiguration;

@Configuration
@Import({
        MustacheConfiguration.class,
        CxfBusConfiguration.class,
        ControllerConfiguration.class,
        LdapSecurityConfig.class,
        WebSecurityConfig.class
})
@SpringBootApplication
public class QueueTriageWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueueTriageWebApplication.class, args);
    }
}
