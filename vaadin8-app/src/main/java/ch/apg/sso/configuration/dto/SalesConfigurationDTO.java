package ch.apg.sso.configuration.dto;

public class SalesConfigurationDTO {
    private final String message;

    public SalesConfigurationDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SalesConfigurationDTO{" +
               "message='" + message + '\'' +
               '}';
    }
}
