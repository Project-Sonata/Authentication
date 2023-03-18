package com.odeyalo.sonata.authentication.testing.spy;

import com.odeyalo.sonata.authentication.service.sender.MailMessage;
import com.odeyalo.sonata.authentication.service.sender.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple spy that just return all sent messages and do nothing else
 */
public class MailSenderSpy implements MailSender {
    private final List<MailMessage> messages = new ArrayList<>();
    private final Logger logger = LoggerFactory.getLogger(MailSenderSpy.class);

    @Override
    public void send(MailMessage message) {
        this.messages.add(message);
        this.logger.debug("Received and saved the message: {}", message);
    }

    public MailMessage getFirst() {
        if (size() == 0) {
            return null;
        }
        return messages.get(0);
    }

    public MailMessage getLast() {
        if (size() == 0) {
            return null;
        }
        return messages.get(size() - 1);
    }

    public List<MailMessage> getAll() {
        return messages;
    }

    public int size() {
        return messages.size();
    }
}
