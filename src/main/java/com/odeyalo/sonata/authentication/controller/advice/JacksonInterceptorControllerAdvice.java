package com.odeyalo.sonata.authentication.controller.advice;

import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class JacksonInterceptorControllerAdvice implements ResponseBodyAdvice<AdvancedUserRegistrationInfo> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getParameterType().isAssignableFrom(AdvancedUserRegistrationInfo.class);
    }

    @Override
    public AdvancedUserRegistrationInfo beforeBodyWrite(AdvancedUserRegistrationInfo body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        assert body != null;
        body.setCountryCode(LocaleContextHolder.getLocale().getCountry());
        return body;
    }
}
