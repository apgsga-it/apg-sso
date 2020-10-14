package ch.apg.sso.configuration.dto;

public class InternalConfigurationDTO {
    private final String message;

    public InternalConfigurationDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "InternalConfigurationDTO{" +
               "message='" + message + '\'' +
               '}';
    }
}
