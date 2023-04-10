package com.odeyalo.sonata.authentication.support.request;

import java.util.Map;

/**
 * Facade interface that used to provide metadata about request.
 * Note: Request is not depended on HTTP protocol only. It can be used with different protocols and with different environment
 */
public interface SharedRequestMetadata {
    /**
     * Return the protocol of the request
     * @return - protocol that was used for this requst
     */
    String getProtocol();

    /**
     * Headers associated with request
     * @return - parameters associated with request, empty map otherwise
     */
    Map<String, String> getHeaders();

    /**
     * Parameters associated with request
     * @return - parameters associated with request, empty map otherwise
     */
    Map<String, String> getParameters();

    /**
     * Method to obtain the header value associated with key
     * @param key - key associated with value
     * @return - value that was found, null otherwise
     */
    String getHeader(String key);

    /**
     * Method to obtain the parameter value associated with key
     * @param key - key associated with value
     * @return - value that was found, null otherwise
     */
    String getParameter(String key);

    /**
     * Returns the cookie value associated with this key
     * If the protocol doesn't support cookies, then null must be returned
     * @param key - key associated with value
     * @return - cookie value, null otherwise.
     */
    String getCookieValue(String key);
}
