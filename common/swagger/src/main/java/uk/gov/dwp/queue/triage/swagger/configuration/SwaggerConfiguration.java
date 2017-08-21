package uk.gov.dwp.queue.triage.swagger.configuration;

import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.migration.mongo.demo.cxf.client.CxfConfiguration;
import uk.gov.dwp.migration.mongo.demo.cxf.client.FeatureRegistry;
import uk.gov.dwp.migration.mongo.demo.cxf.client.ProviderRegistry;

@Configuration
@Import({
        CxfConfiguration.class
})
public class SwaggerConfiguration {

    @Bean
    public Swagger2Feature swagger2Feature(FeatureRegistry featureRegistry) {
        final Swagger2Feature swagger2Feature = new Swagger2Feature();
        swagger2Feature.setPrettyPrint(true);
        swagger2Feature.setResourcePackage("uk.gov.dwp.queue.triage");
        swagger2Feature.setScan(true);
        swagger2Feature.setScanAllResources(true);
        return featureRegistry.add(swagger2Feature);
    }

    @Bean
    public SwaggerSerializers swaggerWriter(ProviderRegistry providerRegistry) {
        return providerRegistry.add(new SwaggerSerializers());
    }
}
