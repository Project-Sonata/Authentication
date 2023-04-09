package com.odeyalo.sonata.authentication.config.web.filter;

import com.odeyalo.sonata.authentication.support.request.SharedRequestMetadataContainer;
import jakarta.servlet.*;
import com.odeyalo.sonata.authentication.support.web.http.HttpSharedRequestMetadata;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The Filter that will create and set {@link HttpSharedRequestMetadata} in {@link SharedRequestMetadataContainer}
 *
 * Note: The Filter works only with HTTP requests and does not provide ability to use another protocol
 */
public class HttpSharedRequestMetadataBuilderFilter extends GenericFilter {
    private final SharedRequestMetadataContainer container;
    private final Logger logger = LoggerFactory.getLogger(HttpSharedRequestMetadataBuilderFilter.class);

    public HttpSharedRequestMetadataBuilderFilter(SharedRequestMetadataContainer container) {
        this.container = container;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSharedRequestMetadata metadata = new HttpSharedRequestMetadata(request);
        container.setSharedRequestMetadata(metadata);
        logger.debug("Set the shared request metadata in HTTP environment: {}", metadata);
    }
}
