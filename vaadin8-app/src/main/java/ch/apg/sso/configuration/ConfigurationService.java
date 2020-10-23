package ch.apg.sso.configuration;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import ch.apg.sso.config.ApgProperties;
import ch.apg.sso.configuration.dto.ConfigurationDTO;
import ch.apg.sso.configuration.dto.SalesConfigurationDTO;
import ch.apg.sso.security.ApgSecurityUtils;
import ch.apg.sso.security.AuthorityConstant;
import ch.apg.sso.user.UserService;
import ch.apg.sso.util.ApgDefaultProfileUtils;

@Service
public class ConfigurationService {
    private final Environment env;

    private final ApgProperties apgProperties;

    private final UserService userService;

    public ConfigurationService(Environment env, ApgProperties apgProperties, UserService userService) {
        this.env = env;
        this.apgProperties = apgProperties;
        this.userService = userService;
    }

    public ConfigurationDTO getConfiguration() {
        return new ConfigurationDTO(
            apgProperties.getVersion(),
            ApgDefaultProfileUtils.getActiveProfiles(env)
        );
    }

    public SalesConfigurationDTO getSalesConfiguration() {
        if (ApgSecurityUtils.hasCurrentUserAnyAuthority(AuthorityConstant.VERKAUF)) {
            return new SalesConfigurationDTO(
                "Der Benutzer " + userService.getCurrentAccount().getUsername() + " besitzt die Rolle " + AuthorityConstant.VERKAUF.name() + "!" + "\n" + userService.getCurrentAccount()
            );
        } else {
            return new SalesConfigurationDTO(
                "Der Benutzer " + userService.getCurrentAccount().getUsername() + " besitzt die Rolle " + AuthorityConstant.VERKAUF.name() + " nicht!"
            );
        }
    }
}
