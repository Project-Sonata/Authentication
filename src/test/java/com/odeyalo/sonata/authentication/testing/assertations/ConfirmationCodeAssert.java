package com.odeyalo.sonata.authentication.testing.assertations;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.assertj.core.api.AbstractAssert;

import java.util.Optional;

/**
 * Custom assertions for {@link ConfirmationCode} class
 */
public class ConfirmationCodeAssert extends AbstractAssert<ConfirmationCodeAssert, ConfirmationCode> {
    private ConfirmationCode confirmationCode;

    public ConfirmationCodeAssert(ConfirmationCode actual) {
        super(actual, ConfirmationCodeAssert.class);
        this.confirmationCode = actual;
    }

    protected ConfirmationCodeAssert(ConfirmationCode actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public static ConfirmationCodeAssert forCode(ConfirmationCode code) {
        return new ConfirmationCodeAssert(code);
    }

    public static ConfirmationCodeAssert forOptional(Optional<ConfirmationCode> codeOptional) {
        if (codeOptional.isEmpty()) {
            throw new IllegalArgumentException("Optional must contain the element, use notRequired(Optional) method to create ConfirmationCodeAssert from empty Optional");
        }
        return new ConfirmationCodeAssert(codeOptional.get());
    }

    public static ConfirmationCodeAssert notRequired(Optional<ConfirmationCode> codeOptional) {
        return new ConfirmationCodeAssert(codeOptional.orElse(null));
    }

    public ConfirmationCodeAssert hasNotNullCodeValue() {
        if (confirmationCode.getCode() == null) {
            failWithMessage("Generated confirmation code value must be not null!");
        }
        return this;
    }

    public ConfirmationCodeAssert onlyDigits() {
        if (!NumberUtils.isParsable(confirmationCode.getCode())) {
            failWithMessage("Generated confirmation code must contain only digits!");
        }
        return this;
    }

    public ConfirmationCodeAssert specificCodeValueLength(int requiredLength) {
        int actualLength = confirmationCode.getCode().length();
        if (actualLength != requiredLength) {
            failWithMessage("Expected length: %s but was: %s", requiredLength, actualLength);
        }
        return this;
    }

    public ConfirmationCodeAssert shouldBeNotActivated() {
        if (confirmationCode.isActivated()) {
            failWithMessage("Confirmation code must be not activated!");
        }
        return this;
    }

    public ConfirmationCodeAssert shouldBeActivated() {
        if (BooleanUtils.isFalse(confirmationCode.isActivated())) {
            failWithMessage("Confirmation code must be activated!");
        }
        return this;
    }

    public ConfirmationCodeAssert expirationTimeNotNull() {
        if (confirmationCode.getExpirationTime() == null) {
            failWithMessage("Confirmation code expiration time must be not null!");
        }
        return this;
    }

    public ConfirmationCodeAssert expirationTimeNull() {
        if (confirmationCode.getExpirationTime() != null) {
            failWithMessage("Confirmation code expiration time must be null!");
        }
        return this;
    }

    public ConfirmationCodeAssert compareCreatedAndExpirationTime() {
        boolean isExpirationTimeGreater = confirmationCode.getExpirationTime().isAfter(confirmationCode.getCreatedAt());
        if (BooleanUtils.isFalse(isExpirationTimeGreater)) {
            failWithMessage("Expiration time must be greater than creation time!");
        }
        return this;
    }

    public ConfirmationCodeAssert confirmationCodeLifetime(int lifetime) {
        if (!confirmationCode.getExpirationTime().isEqual(confirmationCode.getCreatedAt().plusMinutes(lifetime))) {
            failWithMessage("Lifetime of the code must be same as provided!");
        }
        return this;
    }

    public ConfirmationCodeAssert lifecycleStage(ConfirmationCode.LifecycleStage requiredStage) {
        ConfirmationCode.LifecycleStage actualStage = confirmationCode.getLifecycleStage();
        if (actualStage != requiredStage) {
            failWithMessage("Wrong confirmation code lifecycle stage. Required: %s, actual: %s", requiredStage, actualStage);
        }
        return this;
    }
}
