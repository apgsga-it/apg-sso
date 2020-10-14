package ch.apg.sso.keycloak.storage;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.common.util.MultivaluedHashMap;

import oracle.jdbc.pool.OracleDataSource;

class OracleUserRepositoryCHEI212Test {
    private OracleUserRepository oracleUserRepository;

    @BeforeEach
    void setUp() throws SQLException {
        MultivaluedHashMap<String, String> config = new MultivaluedHashMap<>();
        config.putSingle(OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_URL, "jdbc:oracle:thin:@chei212.apgsga.ch:1521:chei212");
        config.putSingle(OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_USERNAME, "xs_proxy");
        config.putSingle(OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_PASSWORD, "xs_proxy_pass");
        config.putSingle(OracleUserStorageProviderConstants.CONFIG_PROPERTY_DB_PROXY_USERNAME, "PDUSER");

        OracleUserStorageProviderConfig oracleUserStorageProviderConfig = new OracleUserStorageProviderConfig(config);

        OracleDataSource loadUserOracleDataSource = new OracleDataSource();
        loadUserOracleDataSource.setURL(oracleUserStorageProviderConfig.getDbUrl());
        loadUserOracleDataSource.setUser(oracleUserStorageProviderConfig.getDbUsername());
        loadUserOracleDataSource.setPassword(oracleUserStorageProviderConfig.getDbPassword());

        OracleDataSource loginOracleDataSource = new OracleDataSource();
        loginOracleDataSource.setURL(oracleUserStorageProviderConfig.getDbUrl());

        oracleUserRepository = new OracleUserRepository(oracleUserStorageProviderConfig, loadUserOracleDataSource, loginOracleDataSource);
    }

    @Test
    void getCount() {
        int count = oracleUserRepository.getCount();
        assertThat(count).isGreaterThan(1);
    }

    @Test
    void findUserByUsernameTU01() {
        String username = "TU01";

        OracleUserDTO oracleUserDTO = oracleUserRepository.findUserByUsername(username);

        assertThat(oracleUserDTO).isNotNull();
        assertThat(oracleUserDTO.getUsername()).isNotNull().isEqualTo(username);
        assertThat(oracleUserDTO.getFirstName()).isNull();
        assertThat(oracleUserDTO.getLastName()).isNull();
        assertThat(oracleUserDTO.getEmail()).isNull();
        assertThat(oracleUserDTO.getRoles()).isNotNull().hasSize(2).contains("BASISROLLE", "PD_SCHREIBEN");
    }

    @Test
    void findUserByUsername() {
        String username = "STB";

        OracleUserDTO oracleUserDTO = oracleUserRepository.findUserByUsername(username);

        assertThat(oracleUserDTO).isNotNull();
        assertThat(oracleUserDTO.getUsername()).isNotNull().isEqualTo(username);
        assertThat(oracleUserDTO.getFirstName()).isNotNull().isEqualTo("Stefan");
        assertThat(oracleUserDTO.getLastName()).isNotNull().isEqualTo("Brandenberger");
        assertThat(oracleUserDTO.getEmail()).isNotNull().isEqualTo("stefan.brandenberger@apgsga.ch");
        assertThat(oracleUserDTO.getRoles()).isNotNull().isNotEmpty();
    }

    @Test
    void findUserByEmail() {
        String email = "stefan.brandenberger@apgsga.ch";

        OracleUserDTO oracleUserDTO = oracleUserRepository.findUserByEmail(email);

        assertThat(oracleUserDTO).isNotNull();
        assertThat(oracleUserDTO.getUsername()).isNotNull().isEqualTo("STB");
        assertThat(oracleUserDTO.getFirstName()).isNotNull().isEqualTo("Stefan");
        assertThat(oracleUserDTO.getLastName()).isNotNull().isEqualTo("Brandenberger");
        assertThat(oracleUserDTO.getEmail()).isNotNull().isEqualTo(email);
        assertThat(oracleUserDTO.getRoles()).isNotNull().isNotEmpty();
    }

    @Test
    void findUserByUsernameUnknown() {
        String username = "unknown-user";

        OracleUserDTO oracleUserDTO = oracleUserRepository.findUserByUsername(username);

        assertThat(oracleUserDTO).isNull();
    }

    @Test
    void findUserByEmailUnknown() {
        String email = "unknown@user.local";

        OracleUserDTO oracleUserDTO = oracleUserRepository.findUserByEmail(email);

        assertThat(oracleUserDTO).isNull();
    }

    @Test
    void authenticate() {
        String username = "tu01";
        String password = "U3tli_b3rg";

        boolean authenticated = oracleUserRepository.authenticate(username, password);

        assertThat(authenticated).isTrue();
    }

    @Test
    public void testLoginWithWrongUsername() throws SQLException {
        String username = "wrong user";
        String password = "U3tli_b3rg";

        boolean authenticated = oracleUserRepository.authenticate(username, password);

        assertThat(authenticated).isFalse();
    }

    @Test
    public void testLoginWithWrongPassword() throws SQLException {
        String username = "tu01";
        String password = "wrong password";

        boolean authenticated = oracleUserRepository.authenticate(username, password);

        assertThat(authenticated).isFalse();
    }
}
