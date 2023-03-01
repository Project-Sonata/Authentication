package com.odeyalo.sonata.authentication.service.registration;

/**
 * Immutable class that stores info about registration
 * @param success - true if registration was success, false otherwise
 * @param action - required action to activate the user
 */
public record RegistrationResult(boolean success, RequiredAction action) {

    public static RegistrationResult of(boolean success, RequiredAction action) {
        return new RegistrationResult(success, action);
    }

    public static RegistrationResult success(RequiredAction action) {
        return of(true, action);
    }

    public static RegistrationResult failed(RequiredAction action) {
        return of(true, action);
    }

    public record RequiredAction(String actionName) {
        public static final RequiredAction DO_NOTHING = new RequiredAction("DO_NOTHING");
        public static final RequiredAction CONFIRM_EMAIL = new RequiredAction("CONFIRM_EMAIL");
    }
}
