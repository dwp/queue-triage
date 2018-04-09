package org.apache.activemq.junit;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.SslBrokerService;
import org.apache.activemq.broker.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

public class TlsEmbeddedActiveMQBroker extends EmbeddedActiveMQBroker {

    private static final Logger LOGGER = LoggerFactory.getLogger(TlsEmbeddedActiveMQBroker.class);
    private static final String BROKER_URL = "broker:(ssl://localhost:2023)";

    public TlsEmbeddedActiveMQBroker() {
        super(BROKER_URL);
    }

        @Override
    public ActiveMQConnectionFactory createConnectionFactory() {
            String brokerUri = "ssl://localhost:2023";
            ActiveMQSslConnectionFactory activeMQSslConnectionFactory = new ActiveMQSslConnectionFactory(brokerUri);
        try {
            LOGGER.info("Found TLS configuration for broker at: {}. Creating appropriate ssl connection factory.", brokerUri);

            activeMQSslConnectionFactory.setKeyStore(ResourceUtils.getFile("classpath:./tls_activemq/client.ks").getPath());
            activeMQSslConnectionFactory.setKeyStorePassword("clientpassword");
            activeMQSslConnectionFactory.setTrustStore(ResourceUtils.getFile("classpath:./tls_activemq/broker.ks").getPath());
            activeMQSslConnectionFactory.setTrustStorePassword("brokerpassword");
            return activeMQSslConnectionFactory;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void configure() {
        try {
            LOGGER.info("Beginning to configure ActiveMQ embedded with TLS.");
            //loading keystore from file
            KeyStore keystore = KeyStore.getInstance("JKS");

//            File ksfile = new File("/home/me/client1.pkcs12");
            File ksfile = ResourceUtils.getFile("classpath:./tls_activemq/broker.ks");
            FileInputStream ksfis = new FileInputStream(ksfile);
            keystore.load(ksfis, "brokerpassword".toCharArray());

//loading truststore from file
            KeyStore truststore = KeyStore.getInstance("JKS");
//            File tsFile = new File("/home/me/client1.truststore");
            File tsFile = ResourceUtils.getFile("classpath:./tls_activemq/client.ks");
            truststore.load(new FileInputStream(tsFile), "clientpassword"
                .toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
                                                                      .getDefaultAlgorithm());
            kmf.init(keystore, "brokerpassword".toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(truststore);

//broker definition
//        String cfURI = "ssl://localhost:2032";
//        BrokerService brokerService = new BrokerService();
//        brokerService.addConnector(cfURI);

//configure ssl context for the broker
//            SslContext sslContext = new SslContext(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

//need client authentication
            SslContext sslContext = new SslContext(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
            brokerService.setSslContext(sslContext);
//            sslContext.getSSLContext().getDefaultSSLParameters().setNeedClientAuth(true);
//            sslContext.getSSLContext().getDefaultSSLParameters().setWantClientAuth(true);
//            brokerService.addConnector(new URI("ssl://localhost:2023"));
            LOGGER.info("Broker Supported Suites: {}", sslContext.getSSLContext().getDefaultSSLParameters().getAlgorithmConstraints());

        } catch (Exception e) {
            LOGGER.error("An error occurred while attempting to start the active MQ broker in TLS mode.", e);
        }
    }
}
