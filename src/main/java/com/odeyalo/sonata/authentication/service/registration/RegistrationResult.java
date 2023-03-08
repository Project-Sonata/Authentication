package com.odeyalo.sonata.authentication.service.registration;

import com.odeyalo.sonata.authentication.common.ErrorDetails;

/**
 * Immutable class that stores info about registration
 * @param success - true if registration was success, false otherwise
 * @param action - required action to activate the user
 * @param errorDetails - details about error that was occurred, can be null if everything is okay.
 *
 * Using of constructor is not recommended and static methods should be used to create a new RegistrationResult
 */

public record RegistrationResult(boolean success, RequiredAction action, ErrorDetails errorDetails) {

    public RegistrationResult(boolean success, RequiredAction action) {
        this(success, action, null);
    }

    public static RegistrationResult of(boolean success, RequiredAction action) {
        return new RegistrationResult(success, action);
    }

    public static RegistrationResult of(boolean success, RequiredAction action, ErrorDetails errorDetails) {
        return new RegistrationResult(success, action, errorDetails);
    }

    public static RegistrationResult success(RequiredAction action) {
        return of(true, action);
    }

    public static RegistrationResult failed(RequiredAction action) {
        return of(false, action);
    }

    public static RegistrationResult failed(RequiredAction action, ErrorDetails errorDetails) {
        return of(false, action, errorDetails);
    }

    public record RequiredAction(String actionName) {
        public static final RequiredAction DO_NOTHING = new RequiredAction("DO_NOTHING");
        public static final RequiredAction CONFIRM_EMAIL = new RequiredAction("CONFIRM_EMAIL");

        public static RequiredAction of(String actionName) {
            return new RequiredAction(actionName);
        }
    }
}
