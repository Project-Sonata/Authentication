package com.odeyalo.sonata.authentication.controller.support;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Associates the data to provide ability to get data between requests
 */
public interface DataRequestAssociationService {
    /**
     * Associate the data with request to access it lately
     * Note: value can be set saved inside the HttpSession, but it's not necessary.
     * @param key - key that can be used to access value
     * @param value - data to save
     * @param request - current request
     * @param response - response to associate data to.
     */
    void associateData(String key, Object value, HttpServletRequest request, HttpServletResponse response);

    Object get(HttpServletRequest request, String key);


    class AssociatedId {
        private String name;
        private String id;
    }
}
