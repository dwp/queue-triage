package uk.gov.dwp.queue.triage.core.jms.activemq.configuration;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class MessageConsumerApplicationInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
//        applicationContext.addBeanFactoryPostProcessor();
    }
}
