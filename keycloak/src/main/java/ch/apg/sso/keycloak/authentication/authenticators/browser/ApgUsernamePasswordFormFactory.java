package ch.apg.sso.keycloak.authentication.authenticators.browser;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.Config;
import org.keycloak.OAuth2Constants;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.DisplayTypeAuthenticatorFactory;
import org.keycloak.authentication.authenticators.console.ConsoleUsernamePasswordAuthenticator;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.provider.ProviderConfigProperty;

public class ApgUsernamePasswordFormFactory implements AuthenticatorFactory, DisplayTypeAuthenticatorFactory {

    public static final String PROVIDER_ID = "apg-auth-username-password-form";
    public static final ApgUsernamePasswordForm SINGLETON = new ApgUsernamePasswordForm();

    public static final String APG_RESET_PASSWORD_BASE_URL_KEY = "apgResetPasswordBaseUrl";
    public static final String APG_REGISTRATION_BASE_URL_SECRET = "apgRegistrationBaseUrl";

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
        return ConsoleUsernamePasswordAuthenticator.SINGLETON;
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
        return PasswordCredentialModel.TYPE;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    public static final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
        AuthenticationExecutionModel.Requirement.REQUIRED
    };

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public String getDisplayType() {
        return "APG Username Password Form";
    }

    @Override
    public String getHelpText() {
        return "Validates a username and password from login form and allows to configure APG reset password and account registration URL.";
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();

    static {
        ProviderConfigProperty property;
        property = new ProviderConfigProperty();
        property.setName(APG_RESET_PASSWORD_BASE_URL_KEY);
        property.setLabel("APG Reset Password Base URL");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("Base URL to go to reset the password outside of Keycloak");
        CONFIG_PROPERTIES.add(property);
        property = new ProviderConfigProperty();
        property.setName(APG_REGISTRATION_BASE_URL_SECRET);
        property.setLabel("APG Registration Base URL");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("Base URL to go to create an account outside of Keycloak");
        CONFIG_PROPERTIES.add(property);
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return CONFIG_PROPERTIES;
    }
}
