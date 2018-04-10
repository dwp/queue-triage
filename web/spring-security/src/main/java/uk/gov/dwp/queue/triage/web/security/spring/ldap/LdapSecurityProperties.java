package uk.gov.dwp.queue.triage.web.security.spring.ldap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "ldap")
public class LdapSecurityProperties implements InitializingBean {

    static final String DEFAULT_BASE_DN = "dc=dwp,dc=gov,dc=uk";
    static final String DEFAULT_USER_DN_PATTERN = "uid={0},ou=Users";
    static final String DEFAULT_GROUP_SEARCH_BASE = "ou=Groups";
    static final String DEFAULT_PASSWORD_ATTRIBUTE = "userPassword";
    static final String DEFAULT_PASSWORD_ENCODER = "plaintext";

    private List<String> urls = new ArrayList<>();
    private String baseDn = DEFAULT_BASE_DN;
    private GroupSearch groupSearch = new GroupSearch();
    private List<String> userDnPatterns = new ArrayList<>();
    private Password password = new Password();

    public List<String> getUrls() {
        return urls;
    }

    public String getBaseDn() {
        return baseDn;
    }

    public void setBaseDn(String baseDn) {
        this.baseDn = baseDn;
    }

    public GroupSearch getGroupSearch() {
        return groupSearch;
    }

    public List<String> getUserDnPatterns() {
        return userDnPatterns;
    }

    public Password getPassword() {
        return password;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (userDnPatterns.isEmpty()) {
            userDnPatterns.add(DEFAULT_USER_DN_PATTERN);
        }
    }

    public static class GroupSearch {
        private String base = DEFAULT_GROUP_SEARCH_BASE;

        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }
    }

    public static class Password {
        private String attribute = DEFAULT_PASSWORD_ATTRIBUTE;
        private String encoder = DEFAULT_PASSWORD_ENCODER;

        public String getAttribute() {
            return attribute;
        }

        public String getEncoder() {
            return encoder;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        public void setEncoder(String encoder) {
            this.encoder = encoder;
        }
    }
}
