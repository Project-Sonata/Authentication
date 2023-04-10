package com.odeyalo.sonata.authentication.config.web;

import com.odeyalo.sonata.authentication.config.web.filter.HttpSharedRequestMetadataBuilderFilter;
import com.odeyalo.sonata.authentication.support.request.SharedRequestMetadataContainer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to create and register the Filters
 */
@Configuration
public class FilterRegistrationConfiguration {

    @Bean
    public HttpSharedRequestMetadataBuilderFilter httpSharedRequestMetadataBuilderFilter(SharedRequestMetadataContainer container) {
        return new HttpSharedRequestMetadataBuilderFilter(container);
    }

    @Bean
    public FilterRegistrationBean<HttpSharedRequestMetadataBuilderFilter> httpSharedRequestMetadataBuilderFilterFilterRegistrationBean(HttpSharedRequestMetadataBuilderFilter filter) {
        FilterRegistrationBean<HttpSharedRequestMetadataBuilderFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(filter);
        return bean;
    }
}
