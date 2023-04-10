package com.odeyalo.sonata.authentication.testing.mock;

import com.github.javafaker.Faker;
import com.github.javafaker.service.RandomService;
import com.odeyalo.sonata.authentication.support.request.SharedRequestMetadata;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EqualsAndHashCode
@Builder
public class MockSharedRequestMetadata implements SharedRequestMetadata {
    private final Map<String, String> headers = new ConcurrentHashMap<>();
    private final Map<String, String> params = new ConcurrentHashMap<>();
    private final Map<String, String> cookies = new ConcurrentHashMap<>();
    private final String protocol;

    public static final String MOCK_PROTOCOL = "mock";

    public MockSharedRequestMetadata() {
        this.protocol = MOCK_PROTOCOL;
    }

    public MockSharedRequestMetadata(String protocol) {
        this.protocol = protocol;
    }

    public MockSharedRequestMetadata(Map<String, String> headers, Map<String, String> params, Map<String, String> cookies, String  protocol) {
        this.headers.putAll(headers != null ? headers : Collections.emptyMap());
        this.params.putAll(params != null ? params : Collections.emptyMap());
        this.cookies.putAll(cookies != null ? cookies : Collections.emptyMap());
        this.protocol = protocol;
    }

    public static MockSharedRequestMetadata random() {
        Faker faker = new Faker();
        RandomService random = faker.random();
        Integer iterations = random.nextInt(1, 10);
        HashMap<String, String> randomValues = new HashMap<>();

        for (int i = 0; i < iterations; i++) {
            String key = RandomStringUtils.randomAlphabetic(10);
            String value = RandomStringUtils.randomAlphabetic(10);
            randomValues.put(key, value);
        }

        return new MockSharedRequestMetadata(randomValues, randomValues, randomValues, MOCK_PROTOCOL);
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
        return params;
    }

    @Override
    public String getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public String getParameter(String key) {
        return params.get(key);
    }

    @Override
    public String getCookieValue(String key) {
        return cookies.get(key);
    }
}
