package uk.gov.dwp.queue.triage.core.classification.server.configuration;

import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.validation.BindException;

import java.util.Properties;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.MatcherAssert.assertThat;

public class MessageClassificationPropertiesTest {

    private static final String YAML_PROPERTIES_TEMPLATE = "messageClassification:\n  initialDelay: %d\n  frequency:  %d\n  timeUnit: %s";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void loadPropertiesWithValidTimeUnit() throws Exception {
        String yaml = String.format(YAML_PROPERTIES_TEMPLATE, 0, 60, SECONDS);
        AnnotationConfigApplicationContext applicationContext = createApplicationContext(yaml);

        MessageClassificationProperties underTest = applicationContext.getBean(MessageClassificationProperties.class);

        assertThat(underTest.getInitialDelay(), Matchers.is(0L));
        assertThat(underTest.getFrequency(), Matchers.is(60L));
        assertThat(underTest.getTimeUnit(), Matchers.is(SECONDS));
    }

    @Test
    public void loadPropertiesWithInvalidTimeUnit() {
        expectedException.expect(BeanCreationException.class);
        expectedException.expectCause(bindException("timeUnit", "PINEAPPLES"));
        String yaml = String.format(YAML_PROPERTIES_TEMPLATE, 0, 60, "PINEAPPLES");
        AnnotationConfigApplicationContext applicationContext = createApplicationContext(yaml);

        assertThat(applicationContext, Matchers.nullValue());
    }

    private TypeSafeMatcher<BindException> bindException(final String name, final String value) {
        return new TypeSafeMatcher<BindException>() {
            @Override
            protected boolean matchesSafely(BindException bindException) {
                return bindException.getFieldErrorCount() == 1 &&
                        bindException.getFieldError(name).getRejectedValue().equals(value);
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }

    private AnnotationConfigApplicationContext createApplicationContext(String yaml) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setEnvironment(createEnvironment(yaml));
        applicationContext.register(DummyConfiguration.class);
        applicationContext.refresh();
        return applicationContext;
    }

    private StandardEnvironment createEnvironment(String yaml) {
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new PropertiesPropertySource("properties-under-test", loadPropertiesFromYaml(yaml)));
        return environment;
    }

    public Properties loadPropertiesFromYaml(String yaml) {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(new ByteArrayResource(yaml.getBytes()));
        return yamlPropertiesFactoryBean.getObject();
    }

    @Configuration
    @EnableConfigurationProperties(MessageClassificationProperties.class)
    public static class DummyConfiguration {

    }
}