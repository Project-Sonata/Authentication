package com.odeyalo.sonata.authentication.testing.assertations;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.assertj.core.api.AbstractAssert;

import java.time.LocalDateTime;
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

    public EqualsHolder equals(ConfirmationCode code) {
        SmartEqualsConfirmationCodeAssert excluded = new SmartEqualsConfirmationCodeAssert(actual, code, this);
        return new EqualsHolder(excluded,
                () -> {
                    if (!confirmationCode.equals(code)) {
                        failWithMessage("Confirmation codes are not equals! Required: %s but was: %s", confirmationCode, code);
                    }
                    return this;
                });
    }

    public static class EqualsHolder {
        private final SmartEqualsConfirmationCodeAssert smartEqualsConfirmationCodeAssert;
        private final Equals equals;

        public EqualsHolder(SmartEqualsConfirmationCodeAssert smartEqualsConfirmationCodeAssert, Equals equals) {
            this.smartEqualsConfirmationCodeAssert = smartEqualsConfirmationCodeAssert;
            this.equals = equals;
        }

        public ConfirmationCodeAssert check() {
            return equals.check();
        }

        public SmartEqualsConfirmationCodeAssert but() {
            return smartEqualsConfirmationCodeAssert;
        }
    }

    public interface Equals {
        ConfirmationCodeAssert check();
    }

    public static class SmartEqualsConfirmationCodeAssert extends AbstractAssert<SmartEqualsConfirmationCodeAssert, ConfirmationCode> implements Equals {
        private Long requiredId;
        private String requiredCode;
        private LocalDateTime requiredCreatedAt;
        private LocalDateTime requiredExpirationTime;
        private boolean requiredActivatedState;
        private ConfirmationCode.LifecycleStage requiredLifecycleStage;
        // This confirmation code will be compared with all values above
        private final ConfirmationCode equalityChallenger;
        private final ConfirmationCodeAssert parent;

        /**
         * Create a new {@link SmartEqualsConfirmationCodeAssert}
         *
         * @param equalityChallenger - challenger to check equality with other ConfirmationCode
         * @param code               - code that can be customized and compared with equalityChallenger
         * @param parent             - parent ConfirmationCodeAssert, used to create smart builder
         */
        public SmartEqualsConfirmationCodeAssert(ConfirmationCode equalityChallenger, ConfirmationCode code, ConfirmationCodeAssert parent) {
            super(code, SmartEqualsConfirmationCodeAssert.class);
            this.requiredId = code.getId();
            this.requiredCode = code.getCode();
            this.requiredCreatedAt = code.getCreatedAt();
            this.requiredExpirationTime = code.getExpirationTime();
            this.requiredActivatedState = code.isActivated();
            this.requiredLifecycleStage = code.getLifecycleStage();
            this.parent = parent;
            this.equalityChallenger = equalityChallenger;
        }

        public SmartEqualsConfirmationCodeAssert idMustEqualsTo(Long requiredId) {
            this.requiredId = requiredId;
            return this;
        }

        public SmartEqualsConfirmationCodeAssert codeMustEqualTo(String requiredCodeValue) {
            this.requiredCode = requiredCodeValue;
            return this;
        }

        public SmartEqualsConfirmationCodeAssert lifecycleStageMustEqualsTo(ConfirmationCode.LifecycleStage stage) {
            this.requiredLifecycleStage = stage;
            return this;
        }

        public SmartEqualsConfirmationCodeAssert createdAtMustEqualsTo(LocalDateTime createdAt) {
            this.requiredCreatedAt = createdAt;
            return this;
        }

        public SmartEqualsConfirmationCodeAssert expirationTimeMustEqualsTo(LocalDateTime expirationTime) {
            this.requiredExpirationTime = expirationTime;
            return this;
        }

        public SmartEqualsConfirmationCodeAssert activatedMustEqualsTo(boolean activated) {
            this.requiredActivatedState = activated;
            return this;
        }

        @Override
        public ConfirmationCodeAssert check() {
            if (!equalityChallenger.getId().equals(requiredId)) {
                failWithMessage("Wrong ID. Required: %s, actual: %s", requiredId, equalityChallenger.getId());
            }
            if (equalityChallenger.isActivated() != requiredActivatedState) {
                failWithMessage("Wrong 'activated' state. Required: %s, actual: %s", requiredActivatedState, equalityChallenger.isActivated());
            }
            if (equalityChallenger.getLifecycleStage() != requiredLifecycleStage) {
                failWithMessage("Wrong lifecycle stage. Required: %s, actual: %s", requiredLifecycleStage, equalityChallenger.getLifecycleStage());
            }
            if (!equalityChallenger.getCreatedAt().isEqual(requiredCreatedAt)) {
                failWithMessage("Wrong creation time. Required: %s, actual: %s", requiredCreatedAt, equalityChallenger.getCreatedAt());
            }
            if (!equalityChallenger.getExpirationTime().isEqual(requiredExpirationTime)) {
                failWithMessage("Wrong expiration time. Required: %s, actual: %s", requiredExpirationTime, equalityChallenger.getExpirationTime());
            }
            if (!equalityChallenger.getCode().equals(requiredCode)) {
                failWithMessage("Wrong code value. Required: %s, actual: %s", requiredCode, equalityChallenger.getCode());
            }
            return parent;
        }
    }
}
