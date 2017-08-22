package uk.gov.dwp.queue.triage.swagger.configuration;

import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public Swagger2Feature swagger2Feature() {
        final Swagger2Feature swagger2Feature = new Swagger2Feature();
        swagger2Feature.setResourcePackage("uk.gov.dwp.queue.triage");
        swagger2Feature.setScanAllResources(true);
        swagger2Feature.setTitle("Queue Triage Core Application");
        swagger2Feature.setVersion("1.0.0");
        swagger2Feature.setContact("queue-triage@dwp.gov.uk");
        swagger2Feature.setDescription("Microservice for managing dead lettered messages");
        swagger2Feature.setLicense("");
        swagger2Feature.setLicenseUrl("");
        return swagger2Feature;
    }
}