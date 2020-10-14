package ch.apg.sso.config;

import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.HashMap;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import ch.apg.sso.common.filter.CachingHttpHeadersFilter;
import ch.apg.sso.common.filter.MDCServletFilter;
import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory> {

    private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

    private final Environment env;

    private final ApgProperties apgProperties;

    public WebConfigurer(Environment env, ApgProperties apgProperties) {
        this.env = env;
        this.apgProperties = apgProperties;
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        if (env.getActiveProfiles().length != 0) {
            //noinspection RedundantCast
            log.info("Web application configuration, using profiles: {}", (Object[]) env.getActiveProfiles());
        }
        EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
        initMDCFilter(servletContext, dispatcherTypes);
        if (env.acceptsProfiles(Profiles.of(ApgProfileConstants.SPRING_PROFILE_PRODUCTION))) {
            initCachingHttpHeadersFilter(servletContext, dispatcherTypes);
        }
        log.info("Web application fully configured");
    }

    /**
     * Customize the Servlet engine: Mime types, the document root, the cache.
     */
    @Override
    public void customize(WebServerFactory server) {
        setMimeMappings(server);
        // When running in an IDE or with ./mvnw spring-boot:run, set location of the static web assets.
    }

    private void setMimeMappings(WebServerFactory server) {
        if (server instanceof ConfigurableServletWebServerFactory) {
            MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
            // IE issue, see https://github.com/jhipster/generator-jhipster/pull/711
            mappings.add("html", MediaType.TEXT_HTML_VALUE + ";charset=" + StandardCharsets.UTF_8.name().toLowerCase());
            // CloudFoundry issue, see https://github.com/cloudfoundry/gorouter/issues/64
            mappings.add("json", MediaType.TEXT_HTML_VALUE + ";charset=" + StandardCharsets.UTF_8.name().toLowerCase());
            ConfigurableServletWebServerFactory servletWebServer = (ConfigurableServletWebServerFactory) server;
            servletWebServer.setMimeMappings(mappings);
        }
    }

    /**
     * Initializes the caching HTTP Headers Filter.
     */
    private void initCachingHttpHeadersFilter(ServletContext servletContext, EnumSet<DispatcherType> dispatcherTypes) {
        log.debug("Registering Caching HTTP Headers Filter");
        FilterRegistration.Dynamic cachingHttpHeadersFilter = servletContext.addFilter("cachingHttpHeadersFilter", new CachingHttpHeadersFilter(apgProperties));

        cachingHttpHeadersFilter.addMappingForUrlPatterns(dispatcherTypes, true, "*.js");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(dispatcherTypes, true, "*.css");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(dispatcherTypes, true, "*.png");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(dispatcherTypes, true, "*.svg");
        cachingHttpHeadersFilter.setAsyncSupported(true);
    }

    /**
     * Initializes the MDC servlet filter.
     */
    private void initMDCFilter(ServletContext servletContext, EnumSet<DispatcherType> dispatcherTypes) {
        log.debug("Registering MDC Filter");

        FilterRegistration.Dynamic mdcFilter = servletContext.addFilter("mdcServletFilter", new MDCServletFilter());
        mdcFilter.setInitParameters(new HashMap<>());
        mdcFilter.addMappingForUrlPatterns(dispatcherTypes, true, "/*");
        mdcFilter.setAsyncSupported(true);

        FilterRegistration.Dynamic mdcInsertingFilter = servletContext.addFilter("mdcInsertingServletFilter", new MDCInsertingServletFilter());
        mdcInsertingFilter.setInitParameters(new HashMap<>());
        mdcInsertingFilter.addMappingForUrlPatterns(dispatcherTypes, true, "/*");
        mdcInsertingFilter.setAsyncSupported(true);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = apgProperties.getCors();
        if (config.getAllowedOrigins() != null && !config.getAllowedOrigins().isEmpty()) {
            log.debug("Registering CORS filter");
            source.registerCorsConfiguration("/api/**", config);
        }
        return new CorsFilter(source);
    }
}
