package uk.gov.dwp.queue.triage.ldap;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldif.LDIFReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static com.unboundid.ldap.listener.InMemoryListenerConfig.createLDAPConfig;

public class InMemoryDirectoryServerFactory {

    private static final String BASE_DN_KEY   = "ldap.basedn";
    private static final String LDAP_PORT_KEY = "ldap.port";

    private static final String DEFAULT_BASE_DN   = "dc=dwp,dc=gov,dc=uk";
    private static final String DEFAULT_LDAP_PORT = "8389";
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryDirectoryServerFactory.class);

    private Properties properties;

    public InMemoryDirectoryServerFactory() {
        this(new Properties());
    }

    public InMemoryDirectoryServerFactory(Properties properties) {
        this.properties = properties;
    }

    public InMemoryDirectoryServer createDirectoryServer() throws LDAPException {
        InMemoryDirectoryServer server = new InMemoryDirectoryServer(createServerConfig());
        LOGGER.info("Starting LDAP Server on port: {}", server.getListenPort());
        server.startListening();
        return server;
    }

    private InMemoryDirectoryServerConfig createServerConfig() throws LDAPException {
        InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(
            properties.getProperty(BASE_DN_KEY, DEFAULT_BASE_DN)
        );
        String property = properties.getProperty(LDAP_PORT_KEY, DEFAULT_LDAP_PORT);
        LOGGER.info("Resolved the following port:{}", property);
        config.setListenerConfigs(createLDAPConfig(
                "LDAP",
                Integer.parseInt(property))
        );
        return config;
    }

    public InMemoryDirectoryServer createDirectoryServer(String ldifPath) throws LDAPException {
        InMemoryDirectoryServer server = createDirectoryServer();
        server.importFromLDIF(true, new LDIFReader(getClass().getClassLoader().getResourceAsStream(ldifPath)));
        return server;
    }

    public static void main(String[] args) throws LDAPException {
        new InMemoryDirectoryServerFactory()
                .createDirectoryServer("example.ldif")
        ;
    }
}