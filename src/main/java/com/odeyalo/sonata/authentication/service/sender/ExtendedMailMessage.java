package com.odeyalo.sonata.authentication.service.sender;

import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.support.email.message.EmailMessageTypeCode;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;

/**
 * Extended mail message that can contain body in Key-Value pair
 */
@Getter
public class ExtendedMailMessage extends MailMessage {
    private final Map<String, Object> body;

    public ExtendedMailMessage(@NonNull EmailMessageTypeCode messageType, @NonNull EmailReceiver receiver, byte[] content, @NonNull String subject, Map<String, Object> body) {
        super(messageType, receiver, content, subject);
        this.body = body;
    }
}
