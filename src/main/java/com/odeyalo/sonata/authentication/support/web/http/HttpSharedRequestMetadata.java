package com.odeyalo.sonata.authentication.support.web.http;

import com.odeyalo.sonata.authentication.support.request.SharedRequestMetadata;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.ToString;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link SharedRequestMetadata} implementation that used to store info about HTTP request
 */
@ToString
public class HttpSharedRequestMetadata implements SharedRequestMetadata {
    private final Map<String, String> headers = new ConcurrentHashMap<>();
    private final Map<String, String> parameters = new ConcurrentHashMap<>();
    private final Map<String, String> cookies = new ConcurrentHashMap<>();
    private final String protocol;


    public HttpSharedRequestMetadata(HttpServletRequest request) {
        protocol = request.getProtocol();
        initializeHeaders(request);
        initializeParameters(request);
        initializeCookies(request);
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public String getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public String getParameter(String key) {
        return parameters.get(key);
    }

    @Override
    public String getCookieValue(String key) {
        return cookies.get(key);
    }

    private void initializeHeaders(HttpServletRequest request) {
        request.getHeaderNames().asIterator().forEachRemaining((name) -> {
            String value = request.getHeader(name);
            headers.put(name, value);
        });
    }

    private void initializeParameters(HttpServletRequest request) {
        request.getParameterNames().asIterator().forEachRemaining((name) -> {
            String value = request.getParameter(name);
            parameters.put(name, value);
        });
    }

    private void initializeCookies(HttpServletRequest request) {
        Cookie[] requestCookies = request.getCookies();
        if (requestCookies == null) return;

        Arrays.stream(requestCookies).forEach((cookie) -> {
            String name = cookie.getName();
            String value = cookie.getValue();
            this.cookies.put(name, value);
        });
    }
}
