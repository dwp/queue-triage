package uk.gov.dwp.queue.triage.web.common.mustache.configuration;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import uk.gov.dwp.queue.triage.cxf.CxfConfiguration;
import uk.gov.dwp.queue.triage.cxf.ProviderRegistry;
import uk.gov.dwp.queue.triage.web.common.mustache.MustachePageRenderer;
import uk.gov.dwp.queue.triage.web.common.mustache.PageMessageBodyWriter;

@Configuration
@Import(CxfConfiguration.class)
public class MustacheConfiguration {

    @Bean
    public MustacheFactory mustacheFactory() {
        return new DefaultMustacheFactory("mustache/");
    }

    @Bean
    public MustachePageRenderer mustacheRender() {
        return new MustachePageRenderer(mustacheFactory());
    }

    @Bean
    public PageMessageBodyWriter pageMessageBodyWriter(ProviderRegistry providerRegistry) {
        return providerRegistry
                .add(new PageMessageBodyWriter(mustacheRender()));
    }
}
