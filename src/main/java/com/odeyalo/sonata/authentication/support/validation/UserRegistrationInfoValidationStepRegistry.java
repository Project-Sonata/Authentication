package com.odeyalo.sonata.authentication.support.validation;

import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.step.UserRegistrationInfoValidationStep;

/**
 * Interface to registry the {@link UserRegistrationInfoValidationStep}
 */
public interface UserRegistrationInfoValidationStepRegistry extends ValidationStepRegistry<UserRegistrationInfo> {}

