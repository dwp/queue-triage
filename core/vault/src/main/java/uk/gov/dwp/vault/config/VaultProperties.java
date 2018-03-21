package uk.gov.dwp.vault.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class VaultProperties {

    @NotEmpty
    private String address;
    @NotNull
    private Integer openTimeout;
    @NotNull
    private Integer readTimeout;

    @Valid
    private TokenAuthenticationProperties tokenAuthentication = new TokenAuthenticationProperties();

    private SslProperties ssl = new SslProperties();

    public boolean isTokenBaseAuthentication() {
        return isNotBlank(tokenAuthentication.getTokenPath());
    }

    public boolean isSslConfigSpecified() {
        return isNotBlank(ssl.getSslPemFilePath());
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getOpenTimeout() {
        return openTimeout;
    }

    public void setOpenTimeout(Integer openTimeout) {
        this.openTimeout = openTimeout;
    }

    public Integer getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }

    public TokenAuthenticationProperties getTokenAuthentication() {
        return tokenAuthentication;
    }

    public void setTokenAuthentication(TokenAuthenticationProperties tokenAuthentication) {
        this.tokenAuthentication = tokenAuthentication;
    }

    public SslProperties getSsl() {
        return ssl;
    }

    public void setSsl(SslProperties ssl) {
        this.ssl = ssl;
    }

    public static class TokenAuthenticationProperties {

        @NotEmpty
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
