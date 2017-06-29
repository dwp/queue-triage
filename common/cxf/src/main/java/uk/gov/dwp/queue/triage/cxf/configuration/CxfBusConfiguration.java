package uk.gov.dwp.migration.mongo.demo.cxf.configuration;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import uk.gov.dwp.migration.mongo.demo.cxf.client.CxfConfiguration;
import uk.gov.dwp.migration.mongo.demo.cxf.client.ProviderRegistry;
import uk.gov.dwp.migration.mongo.demo.cxf.client.ResourceRegistry;

import java.util.Arrays;

@Configuration
@Import({
        CxfConfiguration.class,
})
@ImportResource({"classpath:META-INF/cxf/cxf.xml"})
public class CxfBusConfiguration {

    @Bean
    public ServletRegistrationBean cxfServlet() {
        return new ServletRegistrationBean(new CXFServlet(), "/*");
    }

    @Bean
    public Server server(Bus bus,
                         CxfConfiguration cxfConfiguration) {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setServiceBeans(cxfConfiguration.resourceRegistry().getResources());
        endpoint.setAddress("/core");
        endpoint.setProviders(cxfConfiguration.providerRegistry().getProviders());
        endpoint.setBus(bus);
        endpoint.setFeatures(Arrays.asList(loggingFeature()));
        return endpoint.create();
    }

    @Bean
    public ResourceRegistry resourceConfiguration() {
        return new ResourceRegistry();
    }

    @Bean
    public LoggingFeature loggingFeature() {
        return new LoggingFeature();
    }
}
