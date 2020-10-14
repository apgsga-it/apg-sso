package ch.apg.sso.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Utility class for Spring Security.
 */
public final class ApgSecurityUtils {

    private ApgSecurityUtils() {
    }

    public static Optional<String> getCurrentUsername() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
            Map<String, Object> attributes = ((DefaultOidcUser) authentication.getPrincipal()).getAttributes();
            if (attributes.containsKey("preferred_username")) {
                return (String) attributes.get("preferred_username");
            }
        } else if (authentication instanceof JwtAuthenticationToken) {
            return (String) ((JwtAuthenticationToken) authentication).getToken().getClaims().get("preferred_username");
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
               getAuthorities(authentication).noneMatch(authority -> AuthorityConstant.ANONYMOUS.name().equals(authority));
    }

    public static boolean hasCurrentUserAnyAuthority(AuthorityConstant... requestedAuthorities) {
        if (requestedAuthorities == null) {
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return getAuthorities(authentication).anyMatch(grantedAuthority -> Stream.of(requestedAuthorities).anyMatch(requestedAuthority -> grantedAuthority.equals(requestedAuthority.name())));
    }

    private static Stream<String> getAuthorities(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication instanceof JwtAuthenticationToken ?
                                                             extractAuthorityFromClaims(((JwtAuthenticationToken) authentication).getToken().getClaims())
                                                                                                              : authentication.getAuthorities();
        return authorities.stream()
                          .map(GrantedAuthority::getAuthority);
    }

    public static List<GrantedAuthority> extractAuthorityFromClaims(Map<String, Object> claims) {
        return mapRolesToGrantedAuthorities(getRolesFromClaims(claims));
    }

    @SuppressWarnings("unchecked")
    private static Collection<String> getRolesFromClaims(Map<String, Object> claims) {
        return (Collection<String>) claims.getOrDefault("apg-roles",
                                                        claims.getOrDefault("roles", new ArrayList<>()));
    }

    private static List<GrantedAuthority> mapRolesToGrantedAuthorities(Collection<String> roles) {
        Set<String> allKnownAuthorities = Arrays.stream(AuthorityConstant.values()).map(Enum::name).collect(Collectors.toSet());
        return roles.stream()
                    .filter(allKnownAuthorities::contains)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
    }
}
