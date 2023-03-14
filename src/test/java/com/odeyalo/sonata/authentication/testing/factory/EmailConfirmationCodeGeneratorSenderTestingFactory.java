package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.service.confirmation.ConfirmationCodeGenerator;
import com.odeyalo.sonata.authentication.service.confirmation.DefaultEmailConfirmationCodeGeneratorSender;
import com.odeyalo.sonata.authentication.service.confirmation.EmailConfirmationCodeGeneratorSender;
import com.odeyalo.sonata.authentication.service.confirmation.NumericConfirmationCodeGenerator;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeEmailMessageCreator;
import com.odeyalo.sonata.authentication.service.sender.MailSender;
import com.odeyalo.sonata.authentication.testing.spy.EmptyEmailConfirmationCodeGeneratorSenderSpy;
import com.odeyalo.sonata.authentication.testing.spy.MailSenderSpy;
import lombok.Getter;

/**
 * Factory to produce {@link EmailConfirmationCodeGeneratorSender} for tests
 */
public class EmailConfirmationCodeGeneratorSenderTestingFactory {

    public static EmailConfirmationCodeGeneratorSender create() {
        return new EmptyEmailConfirmationCodeGeneratorSenderSpy();
    }

    public static DefaultEmailConfirmationCodeGeneratorSenderBuilder createDefaultImplBuilder() {
        return new DefaultEmailConfirmationCodeGeneratorSenderBuilder();
    }

    public static DefaultEmailConfirmationCodeGeneratorSender createDefaultImpl() {
        return createDefaultImplBuilder().build();
    }

    @Getter
    public static class DefaultEmailConfirmationCodeGeneratorSenderBuilder {
        private ConfirmationCodeGenerator generator = new NumericConfirmationCodeGenerator();
        private ConfirmationCodeEmailMessageCreator bodyCreator = ConfirmationCodeEmailMessageCreatorTestingFactory.plainText();
        private MailSender sender = new MailSenderSpy();

        public DefaultEmailConfirmationCodeGeneratorSenderBuilder overrideGenerator(ConfirmationCodeGenerator generator) {
            this.generator = generator;
            return this;
        }

        public DefaultEmailConfirmationCodeGeneratorSenderBuilder overrideBodyCreator(ConfirmationCodeEmailMessageCreator bodyCreator) {
            this.bodyCreator = bodyCreator;
            return this;
        }

        public DefaultEmailConfirmationCodeGeneratorSenderBuilder overrideMailSender(MailSender sender) {
            this.sender = sender;
            return this;
        }

        public DefaultEmailConfirmationCodeGeneratorSender build() {
            return new DefaultEmailConfirmationCodeGeneratorSender(generator, bodyCreator, sender);
        }
    }
}
