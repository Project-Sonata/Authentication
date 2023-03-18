package com.odeyalo.sonata.authentication.exceptions;

/**
 * Checked exception that can be thrown when message sending is failed.
 * It can be used when email message sending is failed or other exceptions can be wrapped in this
 */
public class MessageSendingFailedException extends Exception {
    public MessageSendingFailedException(String message) {
        super(message);
    }

    public MessageSendingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
