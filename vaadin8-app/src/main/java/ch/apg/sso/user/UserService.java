package ch.apg.sso.user;

import ch.apg.sso.security.AuthorityConstant;
import ch.apg.sso.user.dto.AccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static class AccountResourceException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        private AccountResourceException(String message) {
            super(message);
        }
    }

    @Autowired
    private HttpServletRequest httpServletRequest;

    private final ClientRegistration clientRegistration;

    public UserService(ClientRegistrationRepository registrations) {
        this.clientRegistration = registrations.findByRegistrationId("keycloak");
    }

    public AccountDTO getCurrentAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken) {
            Map<String, Object> attributes = ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttributes();
            Collection<? extends GrantedAuthority> authorities = ((OAuth2AuthenticationToken) authentication).getAuthorities();
            return getCurrentAccount(attributes, authorities);
        } else if (authentication instanceof JwtAuthenticationToken) {
            Map<String, Object> attributes = ((JwtAuthenticationToken) authentication).getToken().getClaims();
            Collection<? extends GrantedAuthority> authorities = ((JwtAuthenticationToken) authentication).getAuthorities();
            return getCurrentAccount(attributes, authorities);
        } else {
            throw new IllegalArgumentException("AuthenticationToken is not OAuth2 or JWT!");
        }
    }

    private static AccountDTO getCurrentAccount(Map<String, Object> attributes, Collection<? extends GrantedAuthority> authorities) {
        AccountDTO account = new AccountDTO();
        if (attributes.get("sub") != null) {
            account.setId((String) attributes.get("sub"));
        }
        if (attributes.get("preferred_username") != null) {
            account.setUsername(((String) attributes.get("preferred_username")));
        }
        if (attributes.get("given_name") != null) {
            account.setFirstName((String) attributes.get("given_name"));
        }
        if (attributes.get("family_name") != null) {
            account.setLastName((String) attributes.get("family_name"));
        }
        if (attributes.get("email") != null) {
            account.setEmail(((String) attributes.get("email")).toLowerCase());
        }
        if (attributes.get("subj-oid") != null) {
            account.setSubjOid((String) attributes.get("subj-oid"));
        }
        if (attributes.get("gepard-nr") != null) {
            account.setGepardNr((String) attributes.get("gepard-nr"));
        }
        account.setAuthorities(authorities.stream().map(grantedAuthority -> AuthorityConstant.valueOf(grantedAuthority.getAuthority())).collect(Collectors.toSet()));
        return account;
    }
}
