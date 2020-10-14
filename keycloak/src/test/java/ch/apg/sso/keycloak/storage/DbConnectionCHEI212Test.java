package ch.apg.sso.keycloak.storage;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;

public class DbConnectionCHEI212Test {
    private static final Logger log = Logger.getLogger(DbConnectionCHEI212Test.class);

    @Test
    public void testSuccessfulLogin() throws SQLException {
        String username = "tu01";
        String password = "U3tli_b3rg";

        OracleDataSource datasource = new OracleDataSource();
        datasource.setURL("jdbc:oracle:thin:@chei212.apgsga.ch:1521:chei212");

        try (Connection connection = datasource.getConnection(username, password)) {
            log.debugv("authentication successful for {0}", username);
        } catch (SQLException e) {
            log.debugv("authentication failed for {0}", username);
            fail();
        }
    }

    @Test
    public void testLoginWithWrongUsername() throws SQLException {
        String username = "wrong user";
        String password = "U3tli_b3rg";

        OracleDataSource datasource = new OracleDataSource();
        datasource.setURL("jdbc:oracle:thin:@chei212.apgsga.ch:1521:chei212");

        try (Connection connection = datasource.getConnection(username, password)) {
            log.debugv("authentication successful for {0}", username);
            fail();
        } catch (SQLException e) {
            log.debugv("authentication failed for {0}", username);
        }
    }

    @Test
    public void testLoginWithWrongPassword() throws SQLException {
        String username = "tu01";
        String password = "wrong password";

        OracleDataSource datasource = new OracleDataSource();
        datasource.setURL("jdbc:oracle:thin:@chei212.apgsga.ch:1521:chei212");

        try (Connection connection = datasource.getConnection(username, password)) {
            log.debugv("authentication successful for {0}", username);
            fail();
        } catch (SQLException e) {
            log.debugv("authentication failed for {0}", username);
        }
    }

    @Test
    public void testLoadUserByUsernameTu01() throws SQLException {
        String username = "TU01";

        OracleUserDTO oracleUserDTO = getDbUser(username);

        assertThat(oracleUserDTO).isNotNull();
        assertThat(oracleUserDTO.getUsername()).isNotNull().isEqualTo(username);
        assertThat(oracleUserDTO.getFirstName()).isNull();
        assertThat(oracleUserDTO.getLastName()).isNull();
        assertThat(oracleUserDTO.getEmail()).isNull();
        assertThat(oracleUserDTO.getRoles()).isNotNull().hasSize(2).contains("BASISROLLE", "PD_SCHREIBEN");
    }

    @Test
    public void testLoadUserByUsername() throws SQLException {
        String username = "STB";

        OracleUserDTO oracleUserDTO = getDbUser(username);

        assertThat(oracleUserDTO).isNotNull();
        assertThat(oracleUserDTO.getUsername()).isNotNull().isEqualTo(username);
        assertThat(oracleUserDTO.getFirstName()).isNotNull().isEqualTo("Stefan");
        assertThat(oracleUserDTO.getLastName()).isNotNull().isEqualTo("Brandenberger");
        assertThat(oracleUserDTO.getEmail()).isNotNull().isEqualTo("stefan.brandenberger@apgsga.ch");
        assertThat(oracleUserDTO.getRoles()).isNotNull().isNotEmpty();
    }

    @Test
    public void testLoadUserByEmail() throws SQLException {
        String email = "stefan.brandenberger@apgsga.ch";

        OracleUserDTO oracleUserDTO = getDbUser(email);

        assertThat(oracleUserDTO).isNotNull();
        assertThat(oracleUserDTO.getUsername()).isNotNull().isEqualTo("STB");
        assertThat(oracleUserDTO.getFirstName()).isNotNull().isEqualTo("Stefan");
        assertThat(oracleUserDTO.getLastName()).isNotNull().isEqualTo("Brandenberger");
        assertThat(oracleUserDTO.getEmail()).isNotNull().isEqualTo(email);
        assertThat(oracleUserDTO.getRoles()).isNotNull().isNotEmpty();
    }

    @Test
    public void testLoadUknownUserByUsername() throws SQLException {
        String username = "unknown-user";

        OracleUserDTO oracleUserDTO = getDbUser(username);

        assertThat(oracleUserDTO).isNull();
    }

    @Test
    public void testLoadUknownUserByEmail() throws SQLException {
        String username = "unknown@user.local";

        OracleUserDTO oracleUserDTO = getDbUser(username);

        assertThat(oracleUserDTO).isNull();
    }

    private OracleUserDTO getDbUser(String username) throws SQLException {
        String dbUsername = "xs_proxy";
        String dbPassword = "xs_proxy_pass";
        String dbProxyUsername = "PDUSER";

        OracleDataSource datasource = new OracleDataSource();
        datasource.setURL("jdbc:oracle:thin:@chei212.apgsga.ch:1521:chei212");
        datasource.setUser(dbUsername);
        datasource.setPassword(dbPassword);

        try (OracleConnection oracleConnection = (OracleConnection) datasource.getConnection()) {
            final Properties properties = new Properties();
            properties.put(OracleConnection.PROXY_USER_NAME, dbProxyUsername);
            oracleConnection.openProxySession(OracleConnection.PROXYTYPE_USER_NAME, properties);

            String emailToUserQuery = "SELECT username, e_mail_1, kombi_name, rollen FROM gepard_to_anwender_modus_f WHERE LOWER(?) = LOWER(e_mail_1) AND modus = 0";
            String userToEmailQuery = "SELECT username, e_mail_1, kombi_name, rollen FROM gepard_to_anwender_modus_f WHERE UPPER(?) = UPPER(username)";
            boolean isEmail = username.contains("@");
            String query = (isEmail ? emailToUserQuery : userToEmailQuery);

            PreparedStatement statement = oracleConnection.prepareStatement(query);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new OracleUserDTO(
                    resultSet.getString("username"),
                    resultSet.getString("kombi_name"),
                    resultSet.getString("e_mail_1"),
                    resultSet.getString("rollen")
                );
            } else {
                return null;
            }
        }
    }
}
