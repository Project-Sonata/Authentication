package com.odeyalo.sonata.authentication.service.sender;

/**
 * Send the email message in specific way
 */
public interface MailSender {
    /**
     * Send the message to email
     * @param message - message to send
     */
    void send(MailMessage message);

}
