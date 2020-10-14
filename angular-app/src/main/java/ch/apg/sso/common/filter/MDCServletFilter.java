package ch.apg.sso.common.filter;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import ch.apg.sso.security.ApgSecurityUtils;

public class MDCServletFilter extends OncePerRequestFilter {
    public static final String MDC_KEY_USERNAME = "username";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            this.insertIntoMDC();
            filterChain.doFilter(request, response);
        } finally {
            this.clearMDC();
        }
    }

    private void insertIntoMDC() {
        MDC.put(MDC_KEY_USERNAME, getUsername());
    }

    private void clearMDC() {
        MDC.remove(MDC_KEY_USERNAME);
    }

    private String getUsername() {
        String username = StringUtils.EMPTY;

        Optional<String> currentUsernameOptional = ApgSecurityUtils.getCurrentUsername();
        if (ApgSecurityUtils.isAuthenticated() && currentUsernameOptional.isPresent()) {
            username = currentUsernameOptional.get();
        }

        return username;
    }
}
