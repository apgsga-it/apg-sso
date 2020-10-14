package ch.apg.sso.keycloak.authentication.authenticators.browser;

import static ch.apg.sso.keycloak.authentication.authenticators.browser.ApgUsernamePasswordFormFactory.APG_REGISTRATION_BASE_URL_SECRET;
import static ch.apg.sso.keycloak.authentication.authenticators.browser.ApgUsernamePasswordFormFactory.APG_RESET_PASSWORD_BASE_URL_KEY;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.browser.UsernamePasswordForm;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.AuthenticatorConfigModel;

import ch.apg.sso.keycloak.authentication.authenticators.browser.model.ApgBean;

public class ApgUsernamePasswordForm extends UsernamePasswordForm {

    public static final String CLIENT_ID = "client_id";
    public static final String USERNAME = "username";

    @Override
    protected Response challenge(AuthenticationFlowContext context, MultivaluedMap<String, String> formData) {
        handleApgConfig(context);

        return super.challenge(context, formData);
    }

    @Override
    protected Response challenge(AuthenticationFlowContext context, String error) {
        handleApgConfig(context);

        return super.challenge(context, error);
    }

    private void handleApgConfig(AuthenticationFlowContext context) {
        LoginFormsProvider form = context.form();

        UriInfo uriInfo = context.getUriInfo();
        MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();
        String clientId = queryParameters.getFirst(CLIENT_ID);

        AuthenticatorConfigModel authenticatorConfig = context.getAuthenticatorConfig();
        String resetPasswordBaseUrl = authenticatorConfig.getConfig().get(APG_RESET_PASSWORD_BASE_URL_KEY);
        String registrationBaseUrl = authenticatorConfig.getConfig().get(APG_REGISTRATION_BASE_URL_SECRET);

        String resetPasswordUrlWithoutUsername = addUrlParameter(resetPasswordBaseUrl, CLIENT_ID, clientId);
        String registrationUrlWithoutUsername = addUrlParameter(registrationBaseUrl, CLIENT_ID, clientId);

        String resetPasswordUrl = resetPasswordUrlWithoutUsername;
        String registrationUrl = registrationUrlWithoutUsername;

        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String username = formData.getFirst(USERNAME);

        if (StringUtils.isNotBlank(username)) {
            resetPasswordUrl = addUrlParameter(resetPasswordUrlWithoutUsername, USERNAME, username);
            registrationUrl = addUrlParameter(registrationUrlWithoutUsername, USERNAME, username);
        }

        ApgBean apgRegistrationUrl = new ApgBean(
            resetPasswordUrl,
            resetPasswordUrlWithoutUsername,
            registrationUrl,
            registrationUrlWithoutUsername
        );
        form.setAttribute("apg", apgRegistrationUrl);
    }

    private String addUrlParameter(String baseUrl, String parameterName, String parameterValue) {
        StringBuilder sb = new StringBuilder(baseUrl);
        if (baseUrl.contains("?")) {
            sb.append("&");
        } else {
            sb.append("?");
        }
        return sb.append(parameterName)
                 .append("=")
                 .append(parameterValue)
                 .toString();
    }
}
