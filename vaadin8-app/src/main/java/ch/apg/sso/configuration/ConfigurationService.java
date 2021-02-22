package ch.apg.sso.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import ch.apg.sso.config.ApgProperties;
import ch.apg.sso.configuration.dto.ConfigurationDTO;
import ch.apg.sso.configuration.dto.SalesConfigurationDTO;
import ch.apg.sso.security.ApgSecurityUtils;
import ch.apg.sso.security.AuthorityConstant;
import ch.apg.sso.user.UserService;
import ch.apg.sso.util.ApgDefaultProfileUtils;

@Service
public class ConfigurationService {
	private static final Logger log = LoggerFactory.getLogger(ConfigurationService.class);

	private final Environment env;

	private final ApgProperties apgProperties;

	private final UserService userService;

	private final WebClient webClient;

	public ConfigurationService(Environment env, ApgProperties apgProperties, UserService userService,
			WebClient webClient) {
		this.env = env;
		this.apgProperties = apgProperties;
		this.userService = userService;
		this.webClient = webClient;
	}

	public ConfigurationDTO getConfiguration() {
		return new ConfigurationDTO(apgProperties.getVersion(), ApgDefaultProfileUtils.getActiveProfiles(env));
	}

	public SalesConfigurationDTO getSalesConfiguration() {
		log.info("calling rest api of angular app");
		String body = webClient.get().uri("http://angular-app.local:8082/api/configuration/internal").retrieve()
				.bodyToMono(String.class).block();
		log.info("received response from rest api of angular app: {}", body);

		if (ApgSecurityUtils.hasCurrentUserAnyAuthority(AuthorityConstant.VERKAUF)) {
			return new SalesConfigurationDTO("Der Benutzer " + userService.getCurrentAccount().getUsername()
					+ " besitzt die Rolle " + AuthorityConstant.VERKAUF.name() + "!");
		}
		else {
			return new SalesConfigurationDTO("Der Benutzer " + userService.getCurrentAccount().getUsername()
					+ " besitzt die Rolle " + AuthorityConstant.VERKAUF.name() + " nicht!");
		}
	}
}
