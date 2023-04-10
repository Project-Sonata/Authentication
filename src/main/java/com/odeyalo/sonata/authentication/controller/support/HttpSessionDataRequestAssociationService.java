package com.odeyalo.sonata.authentication.controller.support;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Associate the data with HttpSession
 *
 * @see jakarta.servlet.http.HttpSession
 */
@Service
public class HttpSessionDataRequestAssociationService implements DataRequestAssociationService {
    private final String cookieName;
    private final Logger logger = LoggerFactory.getLogger(HttpSessionDataRequestAssociationService.class);

    public HttpSessionDataRequestAssociationService() {
        this.cookieName = "JSESSIONID";
    }

    @Autowired
    public HttpSessionDataRequestAssociationService(@Value("${server.servlet.session.cookie.name:JSESSIONID}") String cookieName) {
        Assert.notNull(cookieName, "Cookie name must be set!");
        this.cookieName = cookieName;
    }

    @PostConstruct
    void setup() {
        logger.info("Service was successfully bootstrapped, using this session cookie name: {}", cookieName);
    }

    @Override
    public AssociatedWith associateData(String key, Object value, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.setAttribute(key, value);
        AssociatedWith store = AssociatedWith.cookieStore(cookieName, session.getId());
        logger.debug("Saved the data in session! Associated request with: {}", store);
        return store;
    }

    @Override
    public Object get(HttpServletRequest request, String key) {
        HttpSession session = request.getSession(false);
        return session != null ? session.getAttribute(key) : null;
    }
}
