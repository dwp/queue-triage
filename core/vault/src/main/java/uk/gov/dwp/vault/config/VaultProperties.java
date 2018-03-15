package uk.gov.dwp.vault.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@ConfigurationProperties(prefix = "vault")
public class VaultProperties {

    private boolean enabled = true;
    private String address;
    private Integer openTimeout;
    private Integer readTimeOut;
    private TokenAuthenticationProperties tokenAuthentication = new TokenAuthenticationProperties();
    private SslProperties ssl = new SslProperties();

    /**
     * If null defaults to "VAULT_ADDR" environment variable
     *
     * @return address of vault server
     */
    public String getAddress() {
        return address;
    }


    /**
     * If null defaults to "VAULT_OPEN_TIMEOUT" environment variable
     *
     * @return open timeout in seconds
     */
    public Integer getOpenTimeout() {
        return openTimeout;
    }

    /**
     * If null Defaults to "VAULT_READ_TIMEOUT" environment variable
     *
     * @return read timeout in seconds
     */
    public Integer getReadTimeOut() {
        return readTimeOut;
    }

    public TokenAuthenticationProperties getTokenAuthentication() {
        return tokenAuthentication;
    }

    public void setTokenAuthentication(TokenAuthenticationProperties tokenAuthentication) {
        this.tokenAuthentication = tokenAuthentication;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setOpenTimeout(Integer openTimeout) {
        this.openTimeout = openTimeout;
    }

    public void setReadTimeOut(Integer readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public boolean isTokenBaseAuthentication() {
        return isNotBlank(tokenAuthentication.getTokenPath());
    }

    public SslProperties getSsl() {
        return ssl;
    }

    public void setSsl(SslProperties ssl) {
        this.ssl = ssl;
    }

    public boolean isSslConfigSpecified() {
        return isNotBlank(ssl.getSslPemFilePath());
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public static class TokenAuthenticationProperties {

        private String tokenPath;

        /**
         * If null defaults to "VAULT_TOKEN" environment variable (if set)
         *
         * @return path to token file for vault server
         */
        public String getTokenPath() {
            return tokenPath;
        }

        public void setTokenPath(String tokenPath) {
            this.tokenPath = tokenPath;
        }
    }


    public static class SslProperties {

        private String sslPemFilePath;
        private Boolean sslVerify;

        /**
         * If defaults to "VAULT_SSL_CERT" environment variable
         *
         * @return file path to cert file
         */
        public String getSslPemFilePath() {
            return sslPemFilePath;
        }

        public void setSslPemFilePath(String sslPemFilePath) {
            this.sslPemFilePath = sslPemFilePath;
        }

        /**
         * If defaults to "VAULT_SSL_VERIFY" environment variable
         *
         * @return if we need to verify the ssl connection
         */
        public Boolean getSslVerify() {
            return sslVerify;
        }

        public void setSslVerify(Boolean sslVerify) {
            this.sslVerify = sslVerify;
        }
    }
}
