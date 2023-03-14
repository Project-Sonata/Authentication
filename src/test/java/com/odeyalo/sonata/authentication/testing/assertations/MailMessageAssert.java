package com.odeyalo.sonata.authentication.testing.assertations;

import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;
import org.apache.commons.lang3.BooleanUtils;
import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

public class MailMessageAssert extends AbstractAssert<MailMessageAssert, MailMessage> {
    private final MailMessage message;

    public MailMessageAssert(MailMessage actual) {
        super(actual, MailMessageAssert.class);
        this.message = actual;
    }

    protected MailMessageAssert(MailMessage actual, Class<?> selfType) {
        super(actual, selfType);
        this.message = actual;
    }

    public static MailMessageAssert forMessage(MailMessage message) {
        return new MailMessageAssert(message);
    }

    public MailMessageAssert empty() {
        contentNull()
                .receiverNull()
                .subjectNull();
        return this;
    }

    public MailMessageAssert subjectNull() {
        if (message.getSubject() != null) {
            failWithMessage("The subject must be null!");
        }
        return this;
    }

    public MailMessageAssert subjectEquals(String required) {
        if (BooleanUtils.isFalse(message.getSubject().equals(required))) {
            failWithMessage("Subject must be equal to: %s", required);
        }
        return this;
    }

    public MailMessageAssert subjectNotEquals(String avoid) {
        if (message.getSubject().equals(avoid)) {
            failWithMessage("Subject must not be equal to: %s", avoid);
        }
        return this;
    }


    public MailMessageAssert subjectNotNull() {
        if (message.getSubject() == null) {
            failWithMessage("The subject must not be null!");
        }
        return this;
    }

    public MailMessageAssert contentNotNull() {
        if (message.getContent() == null) {
            failWithMessage("Content must be not null!");
        }
        return this;
    }

    public MailMessageAssert contentNull() {
        if (message.getContent() != null) {
            failWithMessage("Content must be null!");
        }
        return this;
    }

    public MailMessageAssert exactlyContentEquals(byte[] content) {
        assertThat(message.getContent())
                .as("Content must contain exactly the same as required!")
                .containsExactly(content);
        return this;
    }

    public MailMessageAssert receiverNotNull() {
        if (message.getReceiver() == null) {
            failWithMessage("Receiver must not be null!");
        }
        return this;
    }

    public MailMessageAssert receiverNull() {
        if (message.getReceiver() != null) {
            failWithMessage("Receiver must be null!");
        }
        return this;
    }

    public MailMessageAssert receiverEquals(EmailReceiver required) {
        if (BooleanUtils.isFalse(message.getReceiver().equals(required))) {
            failWithMessage("The EmailReceiver must be equal to: %s", required);
        }
        return this;
    }
}
