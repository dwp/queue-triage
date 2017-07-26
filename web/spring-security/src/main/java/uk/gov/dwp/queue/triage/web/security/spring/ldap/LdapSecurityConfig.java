package uk.gov.dwp.queue.triage.web.security.spring.ldap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;

import static java.util.Collections.singletonList;

@Configuration
public class LdapSecurityConfig {

    @Bean
    public SecurityConfigurerAdapter securityConfigurerAdapter() {
        return new LdapAuthenticationProviderConfigurer()
                .userDnPatterns("uid={0},ou=Users")
                .groupSearchBase("ou=Groups")
                .contextSource(contextSource())
                .passwordCompare()
//                .passwordEncoder(new LdapShaPasswordEncoder())
                .passwordEncoder(new PlaintextPasswordEncoder())
                .passwordAttribute("userPassword")
                .and();
    }

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        return new DefaultSpringSecurityContextSource(
                singletonList("ldap://localhost:8389/"),
                "dc=dwp,dc=gov,dc=uk");
    }
}
