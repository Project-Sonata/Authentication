package com.odeyalo.sonata.authentication.config;

import com.odeyalo.sonata.authentication.service.confirmation.EmailConfirmationCodeGeneratorSender;
import com.odeyalo.sonata.authentication.service.confirmation.LoggingNullEmailConfirmationCodeGeneratorSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailMessageSendingConfiguration {

    @Bean
    public EmailConfirmationCodeGeneratorSender emailConfirmationCodeGeneratorSender() {
        return new LoggingNullEmailConfirmationCodeGeneratorSender();
    }
}
