package uk.gov.dwp.queue.triage.core.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Stream;

@Configuration
public class ApplicationListenerConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationListenerConfiguration.class);

//    @Bean
//    public ApplicationListener<ApplicationReadyEvent> applicationListener() {
//        return event -> Stream.of(event.getApplicationContext().getBeanDefinitionNames())
//                .forEach(LOGGER::debug);
//    }
}
