package uk.gov.dwp.queue.triage.web.common.mustache.configuration;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.queue.triage.web.common.mustache.MustachePageRenderer;

@Configuration
public class MustacheConfiguration {

    @Bean
    public MustacheFactory mustacheFactory() {
        return new DefaultMustacheFactory("mustache/");
    }

    @Bean
    public MustachePageRenderer mustacheRender() {
        return new MustachePageRenderer(mustacheFactory());
    }
}
