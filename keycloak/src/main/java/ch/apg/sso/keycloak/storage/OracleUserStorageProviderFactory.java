package ch.apg.sso.keycloak.storage;

import java.util.List;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;

import oracle.jdbc.pool.OracleDataSource;

public class OracleUserStorageProviderFactory implements UserStorageProviderFactory<OracleUserStorageProvider> {
    private static final Logger log = Logger.getLogger(OracleUserStorageProviderFactory.class);

    @Override
    public String getId() {
        return "apg-oracle-user-storage-provider";
    }

    @Override
    public String getHelpText() {
        return "APG|SGA SSO Keycloak User Storage SPI";
    }

    @Override
    public void init(Config.Scope config) {
        log.infov("initializing factory");
    }

    @Override
    public OracleUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        log.infov("create provider");

        OracleUserRepository oracleUserRepository = setUpDbUserRepository(model.getConfig());

        return new OracleUserStorageProvider(session, model, oracleUserRepository);
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {
        log.infov("validate configuration");

        OracleUserRepository oracleUserRepository = setUpDbUserRepository(config.getConfig());
        try {
            oracleUserRepository.getCount();
        } catch (Exception e) {
            String errorMessage = "error occurred while testing data sources";
            log.error(errorMessage, e);
            throw new ComponentValidationException(errorMessage, e);
        }
    }

    private OracleUserRepository setUpDbUserRepository(MultivaluedHashMap<String, String> config) {
        OracleUserStorageProviderConfig oracleUserStorageProviderConfig = getOracleUserStorageProviderConfig(config);

        try {
            OracleDataSource loadUserOracleDataSource = new OracleDataSource();
            loadUserOracleDataSource.setURL(oracleUserStorageProviderConfig.getDbUrl());
            loadUserOracleDataSource.setUser(oracleUserStorageProviderConfig.getDbUsername());
            loadUserOracleDataSource.setPassword(oracleUserStorageProviderConfig.getDbPassword());

            OracleDataSource loginOracleDataSource = new OracleDataSource();
            loginOracleDataSource.setURL(oracleUserStorageProviderConfig.getDbUrl());

            return new OracleUserRepository(oracleUserStorageProviderConfig, loadUserOracleDataSource, loginOracleDataSource);
        } catch (Exception e) {
            String errorMessage = "error occurred while setting up data sources";
            log.error(errorMessage, e);
            throw new ComponentValidationException(errorMessage, e);
        }
    }

    private OracleUserStorageProviderConfig getOracleUserStorageProviderConfig(MultivaluedHashMap<String, String> config) {
        OracleUserStorageProviderConfig oracleUserStorageProviderConfig = new OracleUserStorageProviderConfig(config);
        boolean dbPasswordDefined = oracleUserStorageProviderConfig.getDbPassword() != null && !oracleUserStorageProviderConfig.getDbPassword().isEmpty();
        log.debugv("configured {0} with {1} = {2}, {3} = {4}, {5} defined = {6}, {7} = {8}, {9} = {10}, {11} = {12}",
                   this,
                   OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_URL,
                   oracleUserStorageProviderConfig.getDbUrl(),
                   OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_USERNAME,
                   oracleUserStorageProviderConfig.getDbUsername(),
                   OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_PASSWORD,
                   dbPasswordDefined,
                   OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_PROXY_USERNAME,
                   oracleUserStorageProviderConfig.getDbProxyUsername(),
                   OracleUserStorageProviderConstants.CONFIG_PROPERTY_VERIFALIA_SID,
                   oracleUserStorageProviderConfig.getVerifaliaSid(),
                   OracleUserStorageProviderConstants.CONFIG_PROPERTY_VERIFALIA_TOKEN,
                   oracleUserStorageProviderConfig.getVerifaliaToken()               
        );
        return oracleUserStorageProviderConfig;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create()
                                           .property()
                                           .name(OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_URL)
                                           .label("Database URL")
                                           .helpText("Database URL used to authenticate users")
                                           .type(ProviderConfigProperty.STRING_TYPE)
                                           .defaultValue("jdbc:oracle:thin:@chei211.apgsga.ch:1521:chei211")
                                           .add()
                                           .property()
                                           .name(OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_USERNAME)
                                           .label("Database Username")
                                           .helpText("Username to connect to the database to load user details")
                                           .type(ProviderConfigProperty.STRING_TYPE)
                                           .defaultValue("xs_proxy")
                                           .add()
                                           .property()
                                           .name(OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_PASSWORD)
                                           .label("Database Password")
                                           .helpText("Password to connect to the database to load user details")
                                           .type(ProviderConfigProperty.PASSWORD)
                                           .defaultValue("xs_proxy_pass")
                                           .add()
                                           .property()
                                           .name(OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_PROXY_USERNAME)
                                           .label("Database Proxy Username")
                                           .helpText("Username to proxy the connection to the database throw when loading user details")
                                           .type(ProviderConfigProperty.STRING_TYPE)
                                           .defaultValue("PDUSER")
                                           .add()
                                           .property()
                                           .name(OracleUserStorageProviderConstants.CONFIG_PROPERTY_VERIFALIA_SID)
                                           .label("Verifalia SID")
                                           .helpText("SID for Verifalia's E-Mail-Checking-Service")
                                           .type(ProviderConfigProperty.STRING_TYPE)
                                           .defaultValue("2765cb75f7de472b85842e83c4f1943c")
                                           .add()
                                           .property()
                                           .name(OracleUserStorageProviderConstants.CONFIG_PROPERTY_VERIFALIA_TOKEN)
                                           .label("Verifalia Token")
                                           .helpText("Token for Verifalia's E-Mail-Checking-Service")
                                           .type(ProviderConfigProperty.STRING_TYPE)
                                           .defaultValue("onvspRsCz7ZHsjj3G2HTNJK8")
                                           .add()
                                           .build();
    }

    @Override
    public void close() {
        log.infov("closing factory");
    }
}
