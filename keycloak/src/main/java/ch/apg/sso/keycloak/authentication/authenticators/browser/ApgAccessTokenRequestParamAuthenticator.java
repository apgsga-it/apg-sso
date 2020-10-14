package ch.apg.sso.keycloak.authentication.authenticators.browser;

import java.util.List;

import org.jboss.logging.Logger;
import org.keycloak.OAuth2Constants;
import org.keycloak.TokenVerifier;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.common.util.Time;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.protocol.LoginProtocol;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.util.TokenUtil;

public class ApgAccessTokenRequestParamAuthenticator implements Authenticator {
    protected static final Logger logger = Logger.getLogger(ApgAccessTokenRequestParamAuthenticator.class);

    private static final TokenVerifier.TokenTypeCheck VALIDATE_ACCESS_TOKEN_REQUEST_PARAMETER = new TokenVerifier.TokenTypeCheck(TokenUtil.TOKEN_TYPE_BEARER);

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        AuthenticationManager.AuthResult authResult = authenticateIdentityRequestParam(context, true);

        if (authResult == null) {
            context.attempted();
        } else {
            AuthenticationSessionModel clientSession = context.getAuthenticationSession();
            LoginProtocol protocol = context.getSession().getProvider(LoginProtocol.class, clientSession.getProtocol());

            //  re-authentication is skipped if re-authentication is required
            if (protocol.requireReauthentication(authResult.getSession(), clientSession)) {
                context.attempted();
            } else {
                context.getSession().setAttribute(AuthenticationManager.SSO_AUTH, "true");

                context.setUser(authResult.getUser());
                context.attachUserSession(authResult.getSession());
                context.success();
            }
        }
    }

    private static AuthenticationManager.AuthResult authenticateIdentityRequestParam(AuthenticationFlowContext context, boolean checkActive) {
        KeycloakSession session = context.getSession();
        RealmModel realm = context.getRealm();

        List<String> tokens = context.getHttpRequest().getUri().getQueryParameters().get(OAuth2Constants.ACCESS_TOKEN);
        if (tokens == null || tokens.isEmpty()) {
            logger.debugv("Could not find request param: {0}", OAuth2Constants.ACCESS_TOKEN);
            return null;
        }

        String tokenString = tokens.get(0);
        if (tokenString == null || "".equals(tokenString)) {
            logger.debugv("Could not find request param: {0}", OAuth2Constants.ACCESS_TOKEN);
            return null;
        }

        AuthenticationManager.AuthResult authResult = AuthenticationManager
            .verifyIdentityToken(session, realm, session.getContext().getUri(), session.getContext().getConnection(), checkActive, false, true, tokenString, session.getContext().getRequestHeaders(),
                                 VALIDATE_ACCESS_TOKEN_REQUEST_PARAMETER);
        if (authResult == null) {
            return null;
        }
        authResult.getSession().setLastSessionRefresh(Time.currentTime());
        return authResult;
    }

    @Override
    public void action(AuthenticationFlowContext context) {

    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void close() {

    }
}
