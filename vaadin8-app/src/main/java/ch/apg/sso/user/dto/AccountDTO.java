package ch.apg.sso.user.dto;

import java.util.Set;

import ch.apg.sso.security.AuthorityConstant;

public class AccountDTO {
	private String id;

	private String username;

	private String firstName;

	private String lastName;

	private String email;

	private String subjOid;
	
	private String gepardNr;

	private Set<AuthorityConstant> authorities;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<AuthorityConstant> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Set<AuthorityConstant> authorities) {
		this.authorities = authorities;
	}

	public String getSubjOid() {
		return subjOid;
	}

	public void setSubjOid(String subjOid) {
		this.subjOid = subjOid;
	}

	public String getGepardNr() {
		return gepardNr;
	}

	public void setGepardNr(String gepardNr) {
		this.gepardNr = gepardNr;
	}

	@Override
	public String toString() {
		return "AccountDTO [id=" + id + ", username=" + username + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + ", subjOid=" + subjOid + ", gepardNr=" + gepardNr + ", authorities="
				+ authorities + "]";
	}
}
