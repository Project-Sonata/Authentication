package com.odeyalo.sonata.authentication.exceptions;

/**
 * IllegalArgumentException subclass that can be thrown when wrong mfa type was received in method.
 */
public class IllegalMfaMethodTypeException extends IllegalArgumentException {
    public IllegalMfaMethodTypeException(String s) {
        super(s);
    }

    public IllegalMfaMethodTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
