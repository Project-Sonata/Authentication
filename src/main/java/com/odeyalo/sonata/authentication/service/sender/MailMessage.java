package com.odeyalo.sonata.authentication.service.sender;

import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Represent message that can be sent to email
 */
@AllArgsConstructor
@Builder
@Data
public class MailMessage {
    protected final EmailReceiver receiver;
    protected final byte[] content;
    protected final String subject;
}
