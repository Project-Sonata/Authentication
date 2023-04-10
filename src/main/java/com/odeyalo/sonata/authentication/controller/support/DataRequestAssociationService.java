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
     *
     * @param key      - key that can be used to access value
     * @param value    - data to save
     * @param request  - current request
     * @param response - response to associate data to.
     * @return - associated key/value pair with this response that was added to it.
     */
    AssociatedWith associateData(String key, Object value, HttpServletRequest request, HttpServletResponse response);

    Object get(HttpServletRequest request, String key);

    /**
     * Record to store the name and value of the saved data
     * @param store - type of the storage that used to save the name and the value
     * @param name - key of the saved header or cookie
     * @param value - value of the saved header or cookie
     */
    record AssociatedWith(AssociatedWith.Store store, String name, String value) {

        public static AssociatedWith headerStore(String name, String value) {
            return new AssociatedWith(Store.HEADER, name, value);
        }

        public static AssociatedWith cookieStore(String name, String value) {
            return new AssociatedWith(Store.COOKIE, name, value);
        }

        public enum Store {
            HEADER, COOKIE
        }
    }
}
