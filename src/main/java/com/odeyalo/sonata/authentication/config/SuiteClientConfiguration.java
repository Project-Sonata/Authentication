package com.odeyalo.sonata.authentication.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.odeyalo.sonata.suite.servlet.client")
public class SuiteClientConfiguration {
}
