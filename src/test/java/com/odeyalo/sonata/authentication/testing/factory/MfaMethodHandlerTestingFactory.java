package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.service.mfa.handler.EmailMfaMethodHandler;
import com.odeyalo.sonata.authentication.testing.factory.builders.EmailMfaMethodHandlerBuilder;

public class MfaMethodHandlerTestingFactory {

    public static EmailMfaMethodHandler emailMfaMethodHandler() {
        return emailMfaMethodHandlerBuilder().build();
    }

    public static EmailMfaMethodHandlerBuilder emailMfaMethodHandlerBuilder() {
        return new EmailMfaMethodHandlerBuilder();
    }
}
