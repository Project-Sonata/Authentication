package com.odeyalo.sonata.authentication.service.confirmation;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.testing.assertations.ConfirmationCodeAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link NumericConfirmationCodeGenerator}
 */
class NumericConfirmationCodeGeneratorTest {

    public static final int LIFETIME_MINUTES = 10;
    public static final int LENGTH = 10;

    @Test
    @DisplayName("Generate confirmation code and expect valid confirmation code with only digits")
    void generateCode_andExpectSuccess() {
        // given
        NumericConfirmationCodeGenerator generator = new NumericConfirmationCodeGenerator();
        // When
        ConfirmationCode confirmationCode = generator.generateCode(LENGTH, LIFETIME_MINUTES);
        // Then
        ConfirmationCodeAssert.forCode(confirmationCode)
                .isNotNull()
                .hasNotNullCodeValue()
                .onlyDigits()
                .shouldBeNotActivated()
                .specificCodeValueLength(LENGTH)
                .confirmationCodeLifetime(LIFETIME_MINUTES);
    }
}
