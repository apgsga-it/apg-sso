package ch.apg.sso.configuration.dto;

import java.util.Arrays;

public class ConfigurationDTO {
    private final String version;

    private final String[] activeProfiles;

    public ConfigurationDTO(String version, String[] activeProfiles) {
        this.version = version;
        this.activeProfiles = activeProfiles;
    }

    public String getVersion() {
        return version;
    }

    public String[] getActiveProfiles() {
        return activeProfiles;
    }

    @Override
    public String toString() {
        return "ConfigurationDTO{" +
               "version='" + version + '\'' +
               ", activeProfiles=" + Arrays.toString(activeProfiles) +
               '}';
    }
}
