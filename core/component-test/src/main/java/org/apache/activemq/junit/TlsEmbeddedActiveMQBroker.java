package org.apache.activemq.junit;

import org.apache.activemq.broker.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

public class TlsEmbeddedActiveMQBroker extends EmbeddedActiveMQBroker {

    private static final Logger LOGGER = LoggerFactory.getLogger(TlsEmbeddedActiveMQBroker.class);
//    private static final String BROKER_URL = "broker:(ssl://localhost:2023)";

//    public TlsEmbeddedActiveMQBroker() {
//        super(BROKER_URL);
//    }
//
//        @Override
//    public ActiveMQConnectionFactory createConnectionFactory() {
//            String brokerUri = "ssl://localhost:2023";
//            ActiveMQSslConnectionFactory activeMQSslConnectionFactory = new ActiveMQSslConnectionFactory(brokerUri);
//        try {
//            LOGGER.info("Found TLS configuration for broker at: {}. Creating appropriate ssl connection factory.", brokerUri);
//
//            activeMQSslConnectionFactory.setKeyStore(ResourceUtils.getFile("classpath:./tls_activemq/client.ks").getPath());
//            activeMQSslConnectionFactory.setKeyStorePassword("clientpassword");
//            activeMQSslConnectionFactory.setTrustStore(ResourceUtils.getFile("classpath:./tls_activemq/client.ts").getPath());
//            activeMQSslConnectionFactory.setTrustStorePassword("clientpassword");
//            return activeMQSslConnectionFactory;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    protected void configure() {
        try {
            LOGGER.info("Beginning to configure ActiveMQ embedded with TLS.");
            //loading keystore from file
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(new FileInputStream(ResourceUtils.getFile("classpath:./tls_activemq/broker.ks")), "brokerpassword".toCharArray());

            KeyStore truststore = KeyStore.getInstance("JKS");
            truststore.load(new FileInputStream(ResourceUtils.getFile("classpath:./tls_activemq/broker.ts")), "brokerpassword".toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keystore, "brokerpassword".toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(truststore);

            SslContext sslContext = new SslContext(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
            brokerService.setSslContext(sslContext);
            brokerService.addConnector("ssl://localhost:2023");

//            sslContext.getSSLContext().getDefaultSSLParameters().setNeedClientAuth(true);
//            sslContext.getSSLContext().getDefaultSSLParameters().setWantClientAuth(true);
            LOGGER.info("Broker Supported Suites: {}", sslContext.getSSLContext().getDefaultSSLParameters().getAlgorithmConstraints());
        } catch (Exception e) {
            LOGGER.error("An error occurred while attempting to start the active MQ broker in TLS mode.", e);
        }
    }
}
