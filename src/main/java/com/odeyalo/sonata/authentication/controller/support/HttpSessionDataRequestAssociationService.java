package com.odeyalo.sonata.authentication.controller.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

/**
 * Associate the data with HttpSession
 *
 * @see jakarta.servlet.http.HttpSession
 */
@Service
public class HttpSessionDataRequestAssociationService implements DataRequestAssociationService {

    @Override
    public void associateData(String key, Object value, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.setAttribute(key, value);
    }

    @Override
    public Object get(HttpServletRequest request, String key) {
        HttpSession session = request.getSession(false);
        return session != null ? session.getAttribute(key) : null;
    }
}
