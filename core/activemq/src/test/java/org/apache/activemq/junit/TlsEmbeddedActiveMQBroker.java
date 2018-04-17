package org.apache.activemq.junit;

import org.apache.activemq.broker.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

public class TlsEmbeddedActiveMQBroker extends EmbeddedActiveMQBroker {

    private static final Logger LOGGER = LoggerFactory.getLogger(TlsEmbeddedActiveMQBroker.class);
    private static final String KEY_STORE_PASSWORD = "brokerpassword";
    private static final String TRUST_STORE_PASSWORD = "brokerpassword";

    @Override
    protected void configure() {
        try {
            LOGGER.info("Beginning to configure ActiveMQ embedded with TLS.");
            //loading keystore from file
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(new FileInputStream(ResourceUtils.getFile("classpath:./tls_activemq/broker.ks")), KEY_STORE_PASSWORD.toCharArray());

            KeyStore truststore = KeyStore.getInstance("JKS");
            truststore.load(new FileInputStream(ResourceUtils.getFile("classpath:./tls_activemq/broker.ts")), TRUST_STORE_PASSWORD.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keystore, KEY_STORE_PASSWORD.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(truststore);

            SslContext sslContext = new SslContext(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
            brokerService.setSslContext(sslContext);
            brokerService.addConnector("ssl://localhost:2023");

            LOGGER.info("Broker Supported Suites: {}", sslContext.getSSLContext().getDefaultSSLParameters().getAlgorithmConstraints());
        } catch (Exception e) {
            LOGGER.error("An error occurred while attempting to start the active MQ broker in TLS mode.", e);
        }
    }
}
