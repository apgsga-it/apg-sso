package ch.apg.sso.keycloak.storage;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

public class OracleUserAdapter extends AbstractUserAdapterFederatedStorage {
    private final OracleUserDTO oracleUserDTO;
    private final String keycloakId;

    public OracleUserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, OracleUserDTO oracleUserDTO) {
        super(session, realm, model);
        this.oracleUserDTO = oracleUserDTO;
        this.keycloakId = StorageId.keycloakId(model, oracleUserDTO.getUsername());
    }

    @Override
    public String getId() {
        return keycloakId;
    }

    @Override
    public String getUsername() {
        return oracleUserDTO.getUsername();
    }

    @Override
    public void setUsername(String username) {
        throw new NotImplementedException("oracle users are idempotent and the username cannot be changed");
    }

    @Override
    public String getEmail() {
        return oracleUserDTO.getEmail();
    }

    @Override
    public void setEmail(String email) {
        throw new NotImplementedException("oracle users are idempotent and the email cannot be changed");
    }

    @Override
    public String getFirstName() {
        return oracleUserDTO.getFirstName();
    }

    @Override
    public void setFirstName(String firstName) {
        throw new NotImplementedException("oracle users are idempotent and the first name cannot be changed");
    }

    @Override
    public String getLastName() {
        return oracleUserDTO.getLastName();
    }

    @Override
    public void setLastName(String lastName) {
        throw new NotImplementedException("oracle users are idempotent and the last name cannot be changed");
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        Map<String, List<String>> attrs = super.getAttributes();
        MultivaluedHashMap<String, String> all = new MultivaluedHashMap<>();
        all.putAll(attrs);
        all.addAll("apg-roles", oracleUserDTO.getRoles());
        return all;
    }

    @Override
    public List<String> getAttribute(String name) {
        Map<String, List<String>> attributes = getAttributes();
        List<String> attribute = attributes.get(name);
        return (attribute != null) ? attribute : Collections.emptyList();
    }
}
