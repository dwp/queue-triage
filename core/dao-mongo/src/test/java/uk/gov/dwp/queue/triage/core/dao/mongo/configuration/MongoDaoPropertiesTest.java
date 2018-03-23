package uk.gov.dwp.queue.triage.core.dao.mongo.configuration;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.collection.IsArrayContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;

import uk.gov.dwp.queue.triage.core.dao.mongo.configuration.MongoDaoProperties.Collection;
import uk.gov.dwp.queue.triage.core.dao.mongo.configuration.MongoDaoProperties.MongoOptions;
import uk.gov.dwp.queue.triage.core.dao.mongo.configuration.MongoDaoProperties.MongoOptions.SSL;
import uk.gov.dwp.queue.triage.core.dao.mongo.configuration.MongoDaoProperties.MongoServerAddress;

import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsArrayContainingInOrder.*;

public class MongoDaoPropertiesTest {

    private final MongoDaoProperties underTest = new MongoDaoProperties();
    private final MongoOptions mongoOptions = new MongoOptions();
    private final SSL ssl = new SSL();

    @Before
    public void setUp() {
        underTest.setOptions(mongoOptions);
        mongoOptions.setSsl(ssl);
    }

    @Test
    public void createMongoConfigurationWithMultipleServers() throws Exception {
        AnnotationConfigApplicationContext applicationContext = createApplicationContext("mongo-dao-multiple-servers-and-auth.yml");

        MongoDaoProperties properties = applicationContext.getBean(MongoDaoProperties.class);

        assertThat(properties.getServerAddresses(), contains(
            serverAddress("db1.server.com", 27017),
            serverAddress("db2.server.com", 27017)
        ));
        assertThat(properties.getDbName(), is("someDatabase"));
        assertThat(properties.getFailedMessage(), is(collection("someCollection", Optional.of("failedMessageUser"), Optional.of("Passw0rd".toCharArray()))));
        assertThat(properties.getOptions(), allOf(
            sslEnabled(true),
            invalidHostnameAllowed(true)
        ));
    }

    @Test
    public void createDefaultMongoConfiguration() {
        AnnotationConfigApplicationContext applicationContext = createApplicationContext("mongo-dao-defaults.yml");

        MongoDaoProperties properties = applicationContext.getBean(MongoDaoProperties.class);

        assertThat(properties.getServerAddresses(), contains(
            serverAddress("localhost", 27017)
        ));
        assertThat(properties.getDbName(), is("queue-triage"));
        assertThat(properties.getFailedMessage(), is(collection("failedMessage", Optional.empty(), Optional.empty())));
        assertThat(properties.getOptions(), allOf(
            sslEnabled(false),
            invalidHostnameAllowed(false)
        ));
    }

    public TypeSafeDiagnosingMatcher<MongoOptions> sslEnabled(boolean sslEnabled) {
        return new TypeSafeDiagnosingMatcher<MongoOptions>() {
            @Override
            protected boolean matchesSafely(MongoOptions mongoOptions, Description description) {
                description.appendText("was ").appendValue(mongoOptions.getSsl().isEnabled());
                return mongoOptions.getSsl().isEnabled() == sslEnabled;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("dao.mongo.options.ssl.enabled=").appendValue(sslEnabled);
            }
        };
    }

    public TypeSafeDiagnosingMatcher<MongoOptions> invalidHostnameAllowed(boolean invalidHostnameAllowed) {
        return new TypeSafeDiagnosingMatcher<MongoOptions>() {
            @Override
            protected boolean matchesSafely(MongoOptions mongoOptions, Description description) {
                description.appendText("was ").appendValue(mongoOptions.getSsl().isInvalidHostnameAllowed());
                return mongoOptions.getSsl().isEnabled() == invalidHostnameAllowed;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("dao.mongo.options.ssl.invalidHostnameAllowed=").appendValue(invalidHostnameAllowed);
            }
        };
    }

    public TypeSafeMatcher<MongoServerAddress> serverAddress(String host, int port) {
        return new TypeSafeMatcher<MongoServerAddress>() {
            @Override
            protected boolean matchesSafely(MongoServerAddress serverAddress) {
                return host.equals(serverAddress.getHost()) && (port == serverAddress.getPort());
            }

            @Override
            public void describeTo(Description description) {
                description
                    .appendText("dao.mongo.serverAddresses.host=").appendText(host)
                    .appendText(", dao.mongo.serverAddress.port=").appendValue(port);
            }
        };
    }

    public TypeSafeDiagnosingMatcher<Collection> collection(String name, Optional<String> username, Optional<char[]> password) {
        return new TypeSafeDiagnosingMatcher<Collection>() {
            @Override
            protected boolean matchesSafely(Collection collection, Description description) {
                return name.equals(collection.getName())
                       && username.equals(collection.getOptionalUsername())
                       && Arrays.equals(password.orElse(new char[]{}), collection.getOptionalPassword().orElse(new char[]{}));
            }

            @Override
            public void describeTo(Description description) {
                description
                    .appendText("dao.mongo.").appendText(name).appendText(".username=").appendValue(username)
                    .appendText(", dao.mongo.").appendText(name).appendText(".password=").appendValue(password);
            }
        };
    }

    private AnnotationConfigApplicationContext createApplicationContext(String yamlFilename) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.setEnvironment(createEnvironment(yamlFilename));
        applicationContext.register(DummyConfiguration.class);
        applicationContext.refresh();
        return applicationContext;
    }

    private StandardEnvironment createEnvironment(String yamlFilename) {
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new PropertiesPropertySource("mongo-dao-properties", loadPropertiesFromYaml(yamlFilename)));
        return environment;
    }

    public Properties loadPropertiesFromYaml(String yamlFilename) {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(new ClassPathResource(yamlFilename));
        return yamlPropertiesFactoryBean.getObject();
    }

    @Configuration
    @EnableConfigurationProperties(MongoDaoProperties.class)
    public static class DummyConfiguration {

    }

}