package uk.gov.dwp.queue.triage.web.security.spring.ldap;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;

import static java.util.Collections.singletonList;

@Configuration
@EnableConfigurationProperties(LdapSecurityProperties.class)
public class LdapSecurityConfig {

    @Bean
    public SecurityConfigurerAdapter securityConfigurerAdapter(LdapSecurityProperties ldapSecurityProperties) {
        return new LdapAuthenticationProviderConfigurer<>()
                .userDnPatterns(ldapSecurityProperties.getUserDnPatterns().toArray(new String[0]))
                .groupSearchBase(ldapSecurityProperties.getGroupSearch().getBase())
                .contextSource(contextSource(ldapSecurityProperties))
                .passwordCompare()
//                .passwordEncoder(new LdapShaPasswordEncoder())
                .passwordEncoder(new PlaintextPasswordEncoder())
                .passwordAttribute(ldapSecurityProperties.getPassword().getAttribute())
                .and();
    }

    @Bean
    public DefaultSpringSecurityContextSource contextSource(LdapSecurityProperties ldapSecurityProperties) {
        return new DefaultSpringSecurityContextSource(
                ldapSecurityProperties.getUrls(),
                ldapSecurityProperties.getBaseDn());
    }
}
