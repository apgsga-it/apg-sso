package ch.apg.sso.keycloak.storage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class OracleUserDTO {
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final List<String> roles;

    public OracleUserDTO(String username, String displayName, String email, String roles) {
        this.username = username;
        this.firstName = StringUtils.isNotBlank(displayName) ? StringUtils.substringBeforeLast(displayName, " ") : null;
        this.lastName = StringUtils.isNotBlank(displayName) ? StringUtils.substringAfterLast(displayName, " ") : null;
        this.email = email;
        this.roles = StringUtils.isNotBlank(roles) ? Arrays.asList(StringUtils.split(roles, ",")) : Collections.emptyList();
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "DbUser{" +
               "username='" + username + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", roles=" + roles +
               '}';
    }
}
