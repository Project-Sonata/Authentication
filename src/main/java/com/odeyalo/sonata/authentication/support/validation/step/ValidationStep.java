package com.odeyalo.sonata.authentication.support.validation.step;

import com.odeyalo.sonata.authentication.common.ExtendedErrorDetails;
import com.odeyalo.sonata.authentication.support.validation.ValidationResult;

/**
 * Simple validation step representation, used to validate the Object by some rule
 * @param <T> - type of object to validate
 */
public interface ValidationStep<T> {

    /**
     * Validate the given object by some rule
     * @param t - object to validate
     * @return - {@link ValidationResult#success()} if check has been passed, {@link ValidationResult#failed(ExtendedErrorDetails)} ()} otherwise
     */
    ValidationResult validate(T t);
}
