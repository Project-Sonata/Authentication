package com.odeyalo.sonata.authentication.config;

import com.odeyalo.sonata.authentication.repository.ConfirmationCodeRepository;
import com.odeyalo.sonata.authentication.service.confirmation.*;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeEmailMessageCreator;
import com.odeyalo.sonata.authentication.service.sender.MailSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailMessageSendingConfiguration {

    @Bean
    public EmailConfirmationCodeGeneratorSender emailConfirmationCodeGeneratorSender(
            ConfirmationCodeGenerator generator,
            ConfirmationCodeEmailMessageCreator creator
    ) {
        return new DefaultEmailConfirmationCodeGeneratorSender(generator, creator,
                (message) -> {
                    System.out.println("Send to user: " + message);
                });
    }

    @Bean
    public ConfirmationCodeManager confirmationCodeManager(ConfirmationCodeRepository repository) {
        return new DelegatingPersistentConfirmationCodeManager(new NumericConfirmationCodeGenerator(),repository);
    }

    @Bean
    public MailSender mailSender() {
        return System.out::println;
    }
}
