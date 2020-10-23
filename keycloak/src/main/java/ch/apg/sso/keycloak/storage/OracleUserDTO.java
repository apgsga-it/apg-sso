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
	private final String subjOid;
	private final String gepardNr;

	public OracleUserDTO(String username, String displayName, String email, String roles, Long subjOid,
			Long gepardNr) {
		this.username = username;
		this.firstName = StringUtils.isNotBlank(displayName) ? StringUtils.substringBeforeLast(displayName, " ") : null;
		this.lastName = StringUtils.isNotBlank(displayName) ? StringUtils.substringAfterLast(displayName, " ") : null;
		this.email = email;
		this.roles = StringUtils.isNotBlank(roles) ? Arrays.asList(StringUtils.split(roles, ","))
				: Collections.emptyList();
		this.subjOid = subjOid == null ? null : subjOid.toString();
		this.gepardNr = gepardNr == null ? null : gepardNr.toString();
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

	public String getSubjOid() {
		return subjOid;
	}

	public String getGepardNr() {
		return gepardNr;
	}

	@Override
	public String toString() {
		return "OracleUserDTO [username=" + username + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + ", roles=" + roles + ", subjOid=" + subjOid + ", gepardNr=" + gepardNr + "]";
	}
}
