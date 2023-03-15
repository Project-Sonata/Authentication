package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeEmailMessageCreator;
import com.odeyalo.sonata.authentication.service.confirmation.support.PlainTextConfirmationCodeEmailMessageCreator;

/**
 * Simple factory to produce {@link ConfirmationCodeEmailMessageCreator} impls for tests
 */
public class ConfirmationCodeEmailMessageCreatorTestingFactory {

    public static ConfirmationCodeEmailMessageCreator create() {
        return new PlainTextConfirmationCodeEmailMessageCreator();
    }

    public static PlainTextConfirmationCodeEmailMessageCreator plainText() {
        return new PlainTextConfirmationCodeEmailMessageCreator();
    }

    public static PlainTextConfirmationCodeEmailMessageCreatorBuilder plainTextBuilder() {
        return new PlainTextConfirmationCodeEmailMessageCreatorBuilder();
    }

    public static class PlainTextConfirmationCodeEmailMessageCreatorBuilder {
        private String subject;
        private String bodyFormat;

        public PlainTextConfirmationCodeEmailMessageCreatorBuilder overrideSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public PlainTextConfirmationCodeEmailMessageCreatorBuilder overrideBodyFormat(String bodyFormat) {
            this.bodyFormat = bodyFormat;
            return this;
        }

        public PlainTextConfirmationCodeEmailMessageCreator build() {
            PlainTextConfirmationCodeEmailMessageCreator creator = new PlainTextConfirmationCodeEmailMessageCreator();
            if (!subject.isEmpty()) {
                creator.overrideSubject(subject);
            }
            if (!bodyFormat.isEmpty()) {
                creator.overrideBodyFormat(bodyFormat);
            }
            return creator;
        }
    }

}
