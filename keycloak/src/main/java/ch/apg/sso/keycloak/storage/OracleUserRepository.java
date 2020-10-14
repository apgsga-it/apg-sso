package ch.apg.sso.keycloak.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import org.keycloak.component.ComponentValidationException;

import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;

public class OracleUserRepository {
    private static final String DB_TABLE = "gepard_to_anwender_modus_f";
    private static final String DB_ATTRIBUTE_USERNAME = "username";
    private static final String DB_ATTRIBUTE_EMAIL = "e_mail_1";
    private static final String DB_ATTRIBUTE_DISPLAY_NAME = "kombi_name";
    private static final String DB_ATTRIBUTE_ROLES = "rollen";
    private static final String DB_ATTRIBUTES = StringUtils.join(Arrays.asList(DB_ATTRIBUTE_USERNAME, DB_ATTRIBUTE_EMAIL, DB_ATTRIBUTE_DISPLAY_NAME, DB_ATTRIBUTE_ROLES), ", ");
    private static final Logger log = Logger.getLogger(OracleUserRepository.class);

    private final OracleUserStorageProviderConfig oracleUserStorageProviderConfig;

    private final OracleDataSource loadUserOracleDataSource;

    private final OracleDataSource loginOracleDataSource;

    public OracleUserRepository(OracleUserStorageProviderConfig oracleUserStorageProviderConfig,
                                OracleDataSource loadUserOracleDataSource,
                                OracleDataSource loginOracleDataSource) {
        this.oracleUserStorageProviderConfig = oracleUserStorageProviderConfig;
        this.loadUserOracleDataSource = loadUserOracleDataSource;
        this.loginOracleDataSource = loginOracleDataSource;
    }

    public int getCount() {
        try (OracleConnection oracleConnection = (OracleConnection) loadUserOracleDataSource.getConnection()) {
            openProxySession(oracleConnection);

            String query = "SELECT count(*) FROM " + DB_TABLE;

            Statement statement = oracleConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return 0;
            }
        } catch (Exception e) {
            String errorMessage = "error occurred while loading users from database";
            log.error(errorMessage, e);
            throw new ComponentValidationException(errorMessage, e);
        }
    }

    public OracleUserDTO findUserByUsername(String username) {
        try (OracleConnection oracleConnection = (OracleConnection) loadUserOracleDataSource.getConnection()) {
            openProxySession(oracleConnection);

            String query = "SELECT " + DB_ATTRIBUTES + " FROM " + DB_TABLE + " WHERE UPPER(" + DB_ATTRIBUTE_USERNAME + ") = UPPER(?)";

            PreparedStatement statement = oracleConnection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            return mapDbUser(resultSet);
        } catch (Exception e) {
            String errorMessage = "error occurred while loading user by username";
            log.error(errorMessage, e);
            throw new ComponentValidationException(errorMessage, e);
        }
    }

    public OracleUserDTO findUserByEmail(String email) {
        try (OracleConnection oracleConnection = (OracleConnection) loadUserOracleDataSource.getConnection()) {
            openProxySession(oracleConnection);

            String query = "SELECT " + DB_ATTRIBUTES + " FROM " + DB_TABLE + " WHERE LOWER(" + DB_ATTRIBUTE_EMAIL + ") = LOWER(?) AND modus = 0";

            PreparedStatement statement = oracleConnection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            return mapDbUser(resultSet);
        } catch (Exception e) {
            String errorMessage = "error occurred while loading user by email";
            log.error(errorMessage, e);
            throw new ComponentValidationException(errorMessage, e);
        }
    }

    private OracleUserDTO mapDbUser(ResultSet resultSet) throws SQLException {
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

    private void openProxySession(OracleConnection oracleConnection) throws SQLException {
        final Properties properties = new Properties();
        properties.put(OracleConnection.PROXY_USER_NAME, oracleUserStorageProviderConfig.getDbProxyUsername());
        oracleConnection.openProxySession(OracleConnection.PROXYTYPE_USER_NAME, properties);
    }

    public boolean authenticate(String username, String password) {
        try (Connection connection = loginOracleDataSource.getConnection(username, password)) {
            log.debugv("authentication successful for username {0}", username);
            return true;
        } catch (SQLException e) {
            log.debugv("authentication failed for username {0}", username);
            return false;
        }
    }
}
