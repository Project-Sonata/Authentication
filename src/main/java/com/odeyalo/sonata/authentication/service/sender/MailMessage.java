package com.odeyalo.sonata.authentication.service.sender;

import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.support.email.message.EmailMessageTypeCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * Represent message that can be sent to email
 */
@AllArgsConstructor
@Builder
@Data
public class MailMessage {
    @NonNull
    protected final EmailMessageTypeCode messageType;
    @NonNull
    protected final EmailReceiver receiver;
    @NonNull
    protected final byte[] content;
    @NonNull
    protected final String subject;
}
