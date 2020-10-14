package ch.apg.sso.keycloak.storage;

import org.keycloak.common.util.MultivaluedHashMap;

public class OracleUserStorageProviderConfig {
    private final MultivaluedHashMap<String, String> config;

    public OracleUserStorageProviderConfig(MultivaluedHashMap<String, String> config) {
        this.config = config;
    }

    public String getDbUrl() {
        return config.getFirst(OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_URL);
    }

    public String getDbUsername() {
        return config.getFirst(OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_USERNAME);
    }

    public String getDbPassword() {
        return config.getFirst(OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_PASSWORD);
    }

    public String getDbProxyUsername() {
        return config.getFirst(OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_PROXY_USERNAME);
    }
}
