package com.odeyalo.sonata.authentication.exceptions;

/**
 * Exception indicates that the confirmation code does not contain the value
 * and confirmation cannot be processed
 */
public class MissingConfirmationCodeValueException extends RuntimeException {

    public MissingConfirmationCodeValueException(String message) {
        super(message);
    }

    public MissingConfirmationCodeValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
