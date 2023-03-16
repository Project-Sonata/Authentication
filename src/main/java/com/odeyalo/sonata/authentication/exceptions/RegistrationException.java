package com.odeyalo.sonata.authentication.exceptions;

/**
 * This is a checked exception that may be thrown when a user registration fails and the user has not been successfully registered
 */
public class RegistrationException extends Exception {

    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
