package com.odeyalo.sonata.authentication.testing.assertations;

import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;
import com.odeyalo.sonata.authentication.support.email.message.EmailMessageTypeCode;
import org.apache.commons.lang3.BooleanUtils;
import org.assertj.core.api.AbstractAssert;
import org.springframework.util.Assert;

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
        Assert.notNull(message, "Message must be not null!");
        return new MailMessageAssert(message);
    }

    public MailMessageAssert empty() {
        content()
                .nullable()
                    .and()
                .receiver()
                .nullable()
                    .and()
                .subject()
                .nullable();
        return this;
    }

    public ContentMailMessageAssert content() {
        return new ContentMailMessageAssert(message, this);
    }

    public SubjectMailMessageAssert subject() {
        return new SubjectMailMessageAssert(message, this);
    }

    public EmailReceiverMailMessageAssert receiver() {
        return new EmailReceiverMailMessageAssert(message, this);
    }

    public MailMessageAssert type(EmailMessageTypeCode expected) {
        if (message.getMessageType() != expected) {
            failWithMessage("The type must be exactly the same!. Expected: %s, Actual: %s", expected, message.getMessageType());
        }
        return this;
    }

    public static class ContentMailMessageAssert extends AbstractAssert<ContentMailMessageAssert, MailMessage> {
        private final MailMessageAssert parentAssert;

        public ContentMailMessageAssert(MailMessage actual, MailMessageAssert parent) {
            super(actual, ContentMailMessageAssert.class);
            this.parentAssert = parent;
        }

        public MailMessageAssert and() {
            return parentAssert;
        }

        @Override
        public ContentMailMessageAssert isNotNull() {
            if (actual.getContent() == null) {
                failWithMessage("Content must be not null!");
            }
            return this;
        }

        public ContentMailMessageAssert nullable() {
            if (actual.getContent() != null) {
                failWithMessage("Content must be null!");
            }
            return this;
        }

        public ContentMailMessageAssert exactlyEquals(byte[] content) {
            assertThat(actual.getContent())
                    .as("Content must contain exactly the same as required!")
                    .containsExactly(content);
            return this;
        }

        public ContentMailMessageAssert exactlyEquals(String content) {
            assertThat(content)
                    .as("Content must be the same as required!")
                    .isEqualTo(new String(actual.getContent()));
            return this;
        }
    }

    public static class SubjectMailMessageAssert extends AbstractAssert<SubjectMailMessageAssert, MailMessage> {
        private final MailMessageAssert parent;

        public SubjectMailMessageAssert(MailMessage actual, MailMessageAssert parent) {
            super(actual, SubjectMailMessageAssert.class);
            this.parent = parent;
        }

        public MailMessageAssert and() {
            return parent;
        }


        @Override
        public SubjectMailMessageAssert isNotNull() {
            if (actual.getSubject() == null) {
                failWithMessage("The subject must not be null!");
            }
            return this;
        }

        public SubjectMailMessageAssert nullable() {
            if (actual.getSubject() != null) {
                failWithMessage("The subject must be null!");
            }
            return this;
        }

        public SubjectMailMessageAssert exactlyEquals(String required) {
            String subject = actual.getSubject();
            if (subject == null || BooleanUtils.isFalse(subject.equals(required))) {
                failWithMessage("Subject must be equal to: \"%s\", but was: \"%s\"", required, subject);
            }
            return this;
        }

        public SubjectMailMessageAssert notEquals(String avoid) {
            if (actual.getSubject().equals(avoid)) {
                failWithMessage("Subject must not be equal to: %s", avoid);
            }
            return this;
        }
    }

    public static class EmailReceiverMailMessageAssert extends AbstractAssert<EmailReceiverMailMessageAssert, MailMessage> {
        private final MailMessageAssert parent;

        public EmailReceiverMailMessageAssert(MailMessage actual, MailMessageAssert parent) {
            super(actual, EmailReceiverMailMessageAssert.class);
            this.parent = parent;
        }

        public MailMessageAssert and() {
            return parent;
        }

        @Override
        public EmailReceiverMailMessageAssert isNotNull() {
            if (actual.getReceiver() == null) {
                failWithMessage("Receiver must not be null!");
            }
            return this;
        }

        public EmailReceiverMailMessageAssert nullable() {
            if (actual.getReceiver() != null) {
                failWithMessage("Receiver must be null!");
            }
            return this;
        }

        public EmailReceiverMailMessageAssert exactlyEquals(EmailReceiver required) {
            EmailReceiver receiver = actual.getReceiver();
            if (receiver == null || BooleanUtils.isFalse(receiver.equals(required))) {
                failWithMessage("The EmailReceiver must be equal to: %s", required);
            }
            return this;
        }
    }
}
