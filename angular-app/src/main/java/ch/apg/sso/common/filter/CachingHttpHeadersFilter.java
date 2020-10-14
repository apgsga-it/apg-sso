package ch.apg.sso.common.filter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import ch.apg.sso.config.ApgProperties;

/**
 * This filter is used in production, to put HTTP cache headers with a long (4 years) expiration time.
 */
public class CachingHttpHeadersFilter implements Filter {

    /**
     * Constant <code>DEFAULT_DAYS_TO_LIVE=1461</code>
     */
    public static final int DEFAULT_DAYS_TO_LIVE = 1461; // 4 years
    /**
     * Constant <code>DEFAULT_SECONDS_TO_LIVE=TimeUnit.DAYS.toMillis(DEFAULT_DAYS_TO_LIVE)</code>
     */
    public static final long DEFAULT_SECONDS_TO_LIVE = TimeUnit.DAYS.toMillis(DEFAULT_DAYS_TO_LIVE);

    private long cacheTimeToLive = DEFAULT_SECONDS_TO_LIVE;

    private final ApgProperties apgProperties;

    public CachingHttpHeadersFilter(ApgProperties apgProperties) {
        this.apgProperties = apgProperties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) {
        cacheTimeToLive = TimeUnit.DAYS.toMillis(apgProperties.getHttp().getCache().getTimeToLiveInDays());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        // Nothing to destroy
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.setHeader("Cache-Control", "max-age=" + cacheTimeToLive + ", public");
        httpResponse.setHeader("Pragma", "cache");

        // Setting Expires header, for proxy caching
        httpResponse.setDateHeader("Expires", cacheTimeToLive + System.currentTimeMillis());

        chain.doFilter(request, response);
    }
}
