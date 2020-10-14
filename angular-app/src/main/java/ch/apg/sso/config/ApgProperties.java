package ch.apg.sso.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Properties are configured in the {@code application.yml} file.
 */
@ConfigurationProperties(prefix = "apg", ignoreUnknownFields = false)
public class ApgProperties {
    private String version;

    private final Http http = new Http();

    private final Security security = new Security();

    private final CorsConfiguration cors = new CorsConfiguration();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Http getHttp() {
        return http;
    }

    public Security getSecurity() {
        return security;
    }

    public CorsConfiguration getCors() {
        return cors;
    }

    public static class Http {

        private final Cache cache = new Cache();

        public Cache getCache() {
            return cache;
        }

        public static class Cache {

            private int timeToLiveInDays = 1461; // 4 years (including leap day)

            public int getTimeToLiveInDays() {
                return timeToLiveInDays;
            }

            public void setTimeToLiveInDays(int timeToLiveInDays) {
                this.timeToLiveInDays = timeToLiveInDays;
            }
        }
    }

    public static class Security {

        private final OAuth2 oauth2 = new OAuth2();

        public OAuth2 getOauth2() {
            return oauth2;
        }

        public static class OAuth2 {
            private List<String> audience = new ArrayList<>();

            public List<String> getAudience() {
                return Collections.unmodifiableList(audience);
            }

            public void setAudience(@NotNull List<String> audience) {
                this.audience.addAll(audience);
            }
        }
    }
}
