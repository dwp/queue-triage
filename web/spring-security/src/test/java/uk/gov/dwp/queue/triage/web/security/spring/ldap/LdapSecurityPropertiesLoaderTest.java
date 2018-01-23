package uk.gov.dwp.queue.triage.web.security.spring.ldap;

import org.junit.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static uk.gov.dwp.queue.triage.web.security.spring.ldap.LdapSecurityProperties.DEFAULT_BASE_DN;
import static uk.gov.dwp.queue.triage.web.security.spring.ldap.LdapSecurityProperties.DEFAULT_GROUP_SEARCH_BASE;
import static uk.gov.dwp.queue.triage.web.security.spring.ldap.LdapSecurityProperties.DEFAULT_PASSWORD_ATTRIBUTE;
import static uk.gov.dwp.queue.triage.web.security.spring.ldap.LdapSecurityProperties.DEFAULT_PASSWORD_ENCODER;
import static uk.gov.dwp.queue.triage.web.security.spring.ldap.LdapSecurityProperties.DEFAULT_USER_DN_PATTERN;

public class LdapSecurityPropertiesLoaderTest {

    @Test
    public void createDefaultLdapConfiguration() {
        AnnotationConfigApplicationContext applicationContext = createApplicationContext("spring-security-ldap-defaults.yml");

        LdapSecurityProperties properties = applicationContext.getBean(LdapSecurityProperties.class);

        assertThat(properties.getUrls(), contains(
                "ldap://localhost:1234"
        ));
        assertThat(properties.getBaseDn(), equalTo(DEFAULT_BASE_DN));
        assertThat(properties.getGroupSearch().getBase(), equalTo(DEFAULT_GROUP_SEARCH_BASE));
        assertThat(properties.getPassword().getAttribute(), equalTo(DEFAULT_PASSWORD_ATTRIBUTE));
        assertThat(properties.getPassword().getEncoder(), equalTo(DEFAULT_PASSWORD_ENCODER));
        assertThat(properties.getUserDnPatterns(), contains(
                DEFAULT_USER_DN_PATTERN
        ));
    }

    @Test
    public void createFullConfiguration() {
        AnnotationConfigApplicationContext applicationContext = createApplicationContext("spring-security-ldap-full.yml");

        LdapSecurityProperties properties = applicationContext.getBean(LdapSecurityProperties.class);

        assertThat(properties.getUrls(), contains(
                "ldap://localhost:888/",
                "ldap://localhost:999/"
        ));
        assertThat(properties.getBaseDn(), equalTo("dc=example,dc=com"));
        assertThat(properties.getGroupSearch().getBase(), equalTo("ou=Support"));
        assertThat(properties.getPassword().getAttribute(), equalTo("pw"));
        assertThat(properties.getPassword().getEncoder(), equalTo("sha256"));
        assertThat(properties.getUserDnPatterns(), contains(
                "uid={0},ou=SupportUsers",
                "uid={0},ou=Admins"
        ));
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
        environment.getPropertySources().addFirst(new PropertiesPropertySource("ldap-properties", loadPropertiesFromYaml(yamlFilename)));
        return environment;
    }

    public Properties loadPropertiesFromYaml(String yamlFilename) {
        YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(new ClassPathResource(yamlFilename));
        return yamlPropertiesFactoryBean.getObject();
    }

    @Configuration
    @EnableConfigurationProperties(LdapSecurityProperties.class)
    public static class DummyConfiguration {

    }
}