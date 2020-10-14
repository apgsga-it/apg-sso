package ch.apg.sso.configuration;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.apg.sso.configuration.dto.ConfigurationDTO;
import ch.apg.sso.configuration.dto.InternalConfigurationDTO;
import ch.apg.sso.security.AuthorityConstantStrings;

@RestController
@RequestMapping("/api/configuration")
public class ConfigurationResource {
    private final ConfigurationService configurationService;

    public ConfigurationResource(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping
    public ConfigurationDTO getConfiguration() {
        return configurationService.getConfiguration();
    }

    @GetMapping("/internal")
    @PreAuthorize("hasRole(\"" + AuthorityConstantStrings.BASISROLLE + "\")")
    public InternalConfigurationDTO getInternalConfiguration() {
        return configurationService.getInternalConfiguration();
    }
}
