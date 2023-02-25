package com.odeyalo.sonata.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.function.Consumer;

/**
 * A simple utility class that provides methods to make working with
 * {@link ObjectMapper} simpler.
 *
 * The ObjectMapper can be easily customized by {@link #customize(Consumer)} method
 */
public class JsonTestUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jackson2HalModule());
    }

    public static <T> T convertToPojo(MvcResult mvcResult, Class<T> requiredClass) throws UnsupportedEncodingException, JsonProcessingException {
        return mapper.readValue(mvcResult.getResponse().getContentAsString(), requiredClass);
    }

    /**
     * The method can be used to customize the {@link ObjectMapper} it specific test cases.
     * @param mapperConsumer - consumer that will customize the ObjectMapper
     */
    public static void customize(Consumer<ObjectMapper> mapperConsumer) {
        mapperConsumer.accept(mapper);
    }
}
