package com.odeyalo.sonata.authentication.service.confirmation;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.exceptions.MessageSendingFailedException;
import com.odeyalo.sonata.authentication.service.confirmation.support.PlainTextConfirmationCodeEmailMessageCreator;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;
import com.odeyalo.sonata.authentication.testing.assertations.MailMessageAssert;
import com.odeyalo.sonata.authentication.testing.factory.ConfirmationCodeEmailMessageCreatorTestingFactory;
import com.odeyalo.sonata.authentication.testing.factory.EmailConfirmationCodeGeneratorSenderTestingFactory;
import com.odeyalo.sonata.authentication.testing.faker.ConfirmationCodeFaker;
import com.odeyalo.sonata.authentication.testing.faker.UserFaker;
import com.odeyalo.sonata.authentication.testing.spy.MailSenderSpy;
import com.odeyalo.sonata.authentication.testing.stubs.ConstructorBasedConfirmationCodeGeneratorStub;
import com.odeyalo.sonata.authentication.testing.stubs.NullConfirmationCodeEmailMessageCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.odeyalo.sonata.authentication.support.email.message.EmailMessageTypeCode.EMAIL_CONFIRMATION_MESSAGE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Contains tests for {@link DefaultEmailConfirmationCodeGeneratorSender}
 */
class DefaultEmailConfirmationCodeGeneratorSenderTest {

    @Test
    @DisplayName("Generate the confirmation code. send it and expect code to be sent")
    void generateAndSend_andExpectCodeToBeSent() throws MessageSendingFailedException {
        // given
        String bodyFormat = "Confirmation code: %s";
        String subject = "Hello there";

        ConfirmationCode confirmationCode = ConfirmationCodeFaker.numeric().get();
        ConstructorBasedConfirmationCodeGeneratorStub generator = new ConstructorBasedConfirmationCodeGeneratorStub(confirmationCode);
        MailSenderSpy mailSenderSpy = new MailSenderSpy();
        User user = UserFaker.create().get();
        PlainTextConfirmationCodeEmailMessageCreator creator = getMessageCreator(bodyFormat, subject);

        DefaultEmailConfirmationCodeGeneratorSender sender = EmailConfirmationCodeGeneratorSenderTestingFactory
                .createDefaultImplBuilder()
                .overrideGenerator(generator)
                .overrideMailSender(mailSenderSpy)
                .overrideBodyCreator(creator)
                .build();

        EmailReceiver receiver = EmailReceiver.of("odeyalo@gmail.com");
        // When
        sender.generateAndSend(user, receiver);
        // then
        MailMessage lastMessage = mailSenderSpy.getLast();
        MailMessageAssert.forMessage(lastMessage)
                .receiver()
                .exactlyEquals(receiver)
                .and()
                .content()
                .exactlyEquals(String.format(bodyFormat, confirmationCode.getCode()))
                .and()
                .subject()
                .exactlyEquals(subject)
                .and()
                .type(EMAIL_CONFIRMATION_MESSAGE);
    }


    @Test
    @DisplayName("Provide null and expect IllegalArgumentException to be thrown")
    void provideNull_andExpectException() {
        DefaultEmailConfirmationCodeGeneratorSender sender = EmailConfirmationCodeGeneratorSenderTestingFactory.createDefaultImpl();
        User user = UserFaker.create().get();

        assertThatThrownBy(() -> sender.generateAndSend(user, null))
                .describedAs("If null was provided as receiver, then IllegalArgumentException must be thrown")
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Verify when confirmation code generator returns null, then MessageSendingFailedException to be thrown")
    void generatorReturnsNull_andExpectException() {
        // given
        ConstructorBasedConfirmationCodeGeneratorStub nullStub = new ConstructorBasedConfirmationCodeGeneratorStub(null);
        User user = UserFaker.create().get();
        EmailReceiver receiver = EmailReceiver.of("odeyalo@gmail.com");
        DefaultEmailConfirmationCodeGeneratorSender generatorSender = EmailConfirmationCodeGeneratorSenderTestingFactory.createDefaultImplBuilder()
                .overrideGenerator(nullStub)
                .build();
        // then
        assertThatThrownBy(() -> {
            generatorSender.generateAndSend(user, receiver);
        })
                .describedAs("If ConfirmationCodeGenerator returns null, then nothing must be sent and MessageSendingFailedException must be thrown!")
                .isExactlyInstanceOf(MessageSendingFailedException.class);
    }

    @Test
    @DisplayName("ConfirmationCodeEmailMessageCreator returns null, then MessageSendingFailedException must be thrown")
    void bodyCreatorReturnsNull_andExpectException() {
        // given
        NullConfirmationCodeEmailMessageCreator nullCreator = new NullConfirmationCodeEmailMessageCreator();
        DefaultEmailConfirmationCodeGeneratorSender sender = EmailConfirmationCodeGeneratorSenderTestingFactory.createDefaultImplBuilder()
                .overrideBodyCreator(nullCreator)
                .build();
        User user = UserFaker.create().get();
        EmailReceiver receiver = EmailReceiver.of("odeyalo@gmail.com");
        // then
        assertThatThrownBy(() -> {
            sender.generateAndSend(user, receiver);
        })
                .describedAs("If body is null, then MessageSendingFailedException must be returned")
                .isExactlyInstanceOf(MessageSendingFailedException.class);
    }

    private PlainTextConfirmationCodeEmailMessageCreator getMessageCreator(String bodyFormat, String subject) {
        return ConfirmationCodeEmailMessageCreatorTestingFactory
                .plainTextBuilder()
                .overrideBodyFormat(bodyFormat)
                .overrideSubject(subject)
                .build();
    }
}
