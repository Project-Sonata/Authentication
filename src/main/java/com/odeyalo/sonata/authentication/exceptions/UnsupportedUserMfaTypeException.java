package com.odeyalo.sonata.authentication.exceptions;

/**
 * Used if the USER didn't register the specific MFA type and should use other MFA type
 */
public class UnsupportedUserMfaTypeException extends Exception {

    public UnsupportedUserMfaTypeException(String message) {
        super(message);
    }

    public UnsupportedUserMfaTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
