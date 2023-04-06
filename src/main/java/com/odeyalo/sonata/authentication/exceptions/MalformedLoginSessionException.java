package com.odeyalo.sonata.authentication.exceptions;

/**
 * Exception that can be thrown when login session is malformed or invalid.
 */
public class MalformedLoginSessionException extends Exception {
    public MalformedLoginSessionException(String message) {
        super(message);
    }

    public MalformedLoginSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
