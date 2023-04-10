package com.odeyalo.sonata.authentication.exceptions;

import lombok.Getter;

/**
 * The class UnsupportedMfaMethodException indicates that unsupported mfa method type was provided
 * and mfa authentication cannot be performed.
 */
@Getter
public class UnsupportedMfaMethodException extends Exception {
    private final String method;

    public UnsupportedMfaMethodException(String message, String method) {
        super(message);
        this.method = method;
    }

    public UnsupportedMfaMethodException(String message, String method, Throwable cause) {
        super(message, cause);
        this.method = method;
    }
}
