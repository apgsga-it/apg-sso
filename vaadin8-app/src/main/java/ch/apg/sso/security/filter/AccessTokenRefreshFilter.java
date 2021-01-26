package ch.apg.sso.security.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

@Component
public class AccessTokenRefreshFilter extends OncePerRequestFilter {

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    private final OAuth2AuthorizedClientManager authorizedClientManager;

    public AccessTokenRefreshFilter(OAuth2AuthorizedClientService oAuth2AuthorizedClientService, OAuth2AuthorizedClientManager authorizedClientManager) {
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
        this.authorizedClientManager = authorizedClientManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

            if (accessToken != null && accessToken.getExpiresAt() != null && Instant.now().isAfter(accessToken.getExpiresAt())) {
                OAuth2AuthorizeRequest.Builder builder = OAuth2AuthorizeRequest.withClientRegistrationId("keycloak").principal(authentication);
                builder.attributes(attributes -> {
                    attributes.put(HttpServletRequest.class.getName(), request);
                    attributes.put(HttpServletResponse.class.getName(), response);
                });
                OAuth2AuthorizeRequest authorizeRequest = builder.build();

                // NOTE:
                // 'authorizedClientManager.authorize()' needs to be executed
                // on a dedicated thread via subscribeOn(Schedulers.boundedElastic())
                // since it performs a blocking I/O operation using RestTemplate internally
                OAuth2AuthorizedClient refreshedAuthorizedClient = Mono.fromSupplier(() -> this.authorizedClientManager.authorize(authorizeRequest)).subscribeOn(Schedulers.boundedElastic()).block();

                if (refreshedAuthorizedClient == null) {
                    // TODO Error Handling
                    SecurityContextHolder.clearContext();
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
