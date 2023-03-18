package com.odeyalo.sonata.authentication.testing.stubs;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.service.confirmation.ConfirmationCodeGenerator;

/**
 * Simple {@link ConfirmationCodeGenerator} stub that always return exactly the same value that was provided in constructor
 */
public class ConstructorBasedConfirmationCodeGeneratorStub implements ConfirmationCodeGenerator {
    private final ConfirmationCode confirmationCode;

    public ConstructorBasedConfirmationCodeGeneratorStub(ConfirmationCode confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    @Override
    public ConfirmationCode generateCode(int length, int lifetimeMinutes) {
        return confirmationCode;
    }
}
