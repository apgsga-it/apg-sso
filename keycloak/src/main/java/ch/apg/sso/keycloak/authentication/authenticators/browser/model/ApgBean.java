package ch.apg.sso.keycloak.authentication.authenticators.browser.model;

public class ApgBean {
    private final String resetPasswordUrl;
    private final String resetPasswordUrlWithoutUsername;
    private final String registrationUrl;
    private final String registrationUrlWithoutUsername;

    public ApgBean(String resetPasswordUrl, String resetPasswordUrlWithoutUsername, String registrationUrl, String registrationUrlWithoutUsername) {
        this.resetPasswordUrl = resetPasswordUrl;
        this.resetPasswordUrlWithoutUsername = resetPasswordUrlWithoutUsername;
        this.registrationUrl = registrationUrl;
        this.registrationUrlWithoutUsername = registrationUrlWithoutUsername;
    }

    public String getResetPasswordUrl() {
        return resetPasswordUrl;
    }

    public String getResetPasswordUrlWithoutUsername() {
        return resetPasswordUrlWithoutUsername;
    }

    public String getRegistrationUrl() {
        return registrationUrl;
    }

    public String getRegistrationUrlWithoutUsername() {
        return registrationUrlWithoutUsername;
    }
}
