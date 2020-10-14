package ch.apg.sso.keycloak.storage;

import org.jboss.logging.Logger;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

public class OracleUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {
    private static final Logger log = Logger.getLogger(OracleUserStorageProvider.class);

    private final KeycloakSession session;
    private final ComponentModel model;
    private final OracleUserRepository oracleUserRepository;

    public OracleUserStorageProvider(KeycloakSession session, ComponentModel model, OracleUserRepository oracleUserRepository) {
        this.session = session;
        this.model = model;
        this.oracleUserRepository = oracleUserRepository;
    }

    @Override
    public void preRemove(RealmModel realm) {
        log.debugv("pre-remove realm: realm = {0}, user = {1}", realm.getId());
    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {
        log.debugv("pre-remove group: realm = {0}, group = {1}", realm.getId(), group.getName());
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {
        log.debugv("pre-remove role: realm = {0}, role = {1}", realm.getId(), role.getName());
    }

    @Override
    public void close() {
        log.debugv("closing");
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        log.debugv("lookup user by id: realm = {0}, userId = {1}", realm.getId(), id);

        String username = StorageId.externalId(id);
        return getUserByUsername(username, realm);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        log.debugv("lookup user by username: realm = {0}, username = {1}", realm.getId(), username);

        OracleUserDTO userByUsernameOrEmail = oracleUserRepository.findUserByUsername(username);
        if (userByUsernameOrEmail == null) {
            log.debugv("could not find user by username: realm = {0}, username = {1}", realm.getId(), username);
            return null;
        }
        return new OracleUserAdapter(session, realm, model, userByUsernameOrEmail);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        log.debugv("lookup user by email: realm = {0}, email = {1}", realm.getId(), email);

        OracleUserDTO userByUsernameOrEmail = oracleUserRepository.findUserByEmail(email);
        if (userByUsernameOrEmail == null) {
            log.debugv("could not find user by email: realm = {0}, email = {1}", realm.getId(), email);
            return null;
        }
        return new OracleUserAdapter(session, realm, model, userByUsernameOrEmail);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        log.debugv("is valid user credential: userId = {0}", user.getId());

        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }

        UserCredentialModel userCredentialModel = (UserCredentialModel) input;
        return oracleUserRepository.authenticate(user.getUsername(), userCredentialModel.getChallengeResponse());
    }
}
