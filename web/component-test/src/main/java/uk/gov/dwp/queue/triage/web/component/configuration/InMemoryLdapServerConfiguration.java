package uk.gov.dwp.queue.triage.web.component.configuration;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.sdk.LDAPException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.dwp.queue.triage.ldap.InMemoryDirectoryServerFactory;

@Configuration
public class InMemoryLdapServerConfiguration {

    @Bean
    public InMemoryDirectoryServer inMemoryDirectoryServer() throws LDAPException {
        return new InMemoryDirectoryServerFactory()
                .createDirectoryServer("example.ldif");
    }
}
