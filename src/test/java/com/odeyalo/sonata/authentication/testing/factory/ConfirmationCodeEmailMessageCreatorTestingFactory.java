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
}
