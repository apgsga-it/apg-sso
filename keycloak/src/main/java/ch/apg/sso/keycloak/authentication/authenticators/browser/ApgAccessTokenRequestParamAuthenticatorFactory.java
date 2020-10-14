package ch.apg.sso.keycloak.authentication.authenticators.browser;

import java.util.List;

import org.keycloak.Config;
import org.keycloak.OAuth2Constants;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.DisplayTypeAuthenticatorFactory;
import org.keycloak.authentication.authenticators.AttemptedAuthenticator;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

public class ApgAccessTokenRequestParamAuthenticatorFactory implements AuthenticatorFactory, DisplayTypeAuthenticatorFactory {
    public static final String PROVIDER_ID = "apg-auth-access-token-request-param";
    static ApgAccessTokenRequestParamAuthenticator SINGLETON = new ApgAccessTokenRequestParamAuthenticator();

    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }

    @Override
    public Authenticator createDisplay(KeycloakSession session, String displayType) {
        if (displayType == null) {
            return SINGLETON;
        }
        if (!OAuth2Constants.DISPLAY_CONSOLE.equalsIgnoreCase(displayType)) {
            return null;
        }
        return AttemptedAuthenticator.SINGLETON;  // ignore this authenticator
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getReferenceCategory() {
        return "access-token-request-param";
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public String getDisplayType() {
        return "APG Access Token Request Param";
    }

    @Override
    public String getHelpText() {
        return "Validates the SSO using access token in request param 'access_token'.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return null;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }
}
