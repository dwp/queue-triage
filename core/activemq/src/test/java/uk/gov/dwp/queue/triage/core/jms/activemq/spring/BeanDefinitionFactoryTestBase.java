package uk.gov.dwp.queue.triage.core.jms.activemq.spring;

import org.junit.After;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import uk.gov.dwp.queue.triage.core.jms.activemq.configuration.JmsListenerConfig;
import uk.gov.dwp.queue.triage.core.service.processor.FailedMessageProcessor;

import java.util.Properties;

import static org.mockito.Mockito.mock;

public class BeanDefinitionFactoryTestBase {

    AnnotationConfigApplicationContext applicationContext;

    @After
    public void tearDown() {
        applicationContext.close();
    }

    protected void createApplicationContext(String yamlFilename) {
        applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setEnvironment(createEnvironment(yamlFilename));
        applicationContext.register(JmsListenerConfig.class, AdditionalConfig.class);
        applicationContext.refresh();
    }

    private StandardEnvironment createEnvironment(String yamlFilename) {
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new PropertiesPropertySource("broker-properties", loadPropertiesFromYaml(yamlFilename)));

        return environment;
    }

    private Properties loadPropertiesFromYaml(String yamlFilename) {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(new ClassPathResource(yamlFilename));
        return yamlPropertiesFactoryBean.getObject();
    }

    protected <T> T getBean(String beanName, Class<T> beanType) {
        try {
            return applicationContext.getBean(beanName, beanType);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    protected int numberOfBeansOfType(Class<?> beanType) {
        return applicationContext.getBeanNamesForType(beanType).length;
    }

    @Configuration
    public static class AdditionalConfig {

        @Bean
        public FailedMessageProcessor failedMessageProcessor() {
            return mock(FailedMessageProcessor.class);
        }

    }
}
