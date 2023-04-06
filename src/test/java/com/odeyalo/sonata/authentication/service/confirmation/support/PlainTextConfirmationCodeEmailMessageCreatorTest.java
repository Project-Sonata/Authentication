package com.odeyalo.sonata.authentication.service.confirmation.support;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;
import com.odeyalo.sonata.authentication.support.email.message.EmailMessageTypeCode;
import com.odeyalo.sonata.authentication.testing.assertations.MailMessageAssert;
import com.odeyalo.sonata.authentication.testing.factory.ConfirmationCodeEmailMessageCreatorTestingFactory;
import com.odeyalo.sonata.authentication.testing.faker.ConfirmationCodeFaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static com.odeyalo.sonata.authentication.support.email.message.EmailMessageTypeCode.EMAIL_CONFIRMATION_MESSAGE;
import static com.odeyalo.sonata.authentication.support.email.message.EmailMessageTypeCode.MFA_EMAIL_CONFIRMATION_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class PlainTextConfirmationCodeEmailMessageCreatorTest {

    @Test
    @DisplayName("Create a message from not ConfirmationCode and expect exception")
    void createMessageWithObject_andExpectException() {
        // given
        PlainTextConfirmationCodeEmailMessageCreator creator = ConfirmationCodeEmailMessageCreatorTestingFactory.plainText();
        String obj = "I am not confirmation code";
        EmailReceiver receiver = EmailReceiver.of("odeyalo@gmail.com");
        // then
        assertThatThrownBy(() -> creator.createMessage(obj, receiver))
                .as("The exception must be thrown if not ConfirmationCode was provided as parameter!")
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Check support method for confirmation code and expect true")
    void supportsForValidTypeAndConfirmationCodeEntity_andExpectTrue() {
        // given
        PlainTextConfirmationCodeEmailMessageCreator creator = ConfirmationCodeEmailMessageCreatorTestingFactory.plainText();
        // when
        boolean actual = creator.supports(EMAIL_CONFIRMATION_MESSAGE, new ConfirmationCode());
        // then
        assertThat(actual)
                .as("If EmailMessageTypeCodeConstants.EMAIL_CONFIRMATION_MESSAGE and ConfirmationCode are using then true must be returned")
                .isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = EmailMessageTypeCode.class , names = {"EMAIL_CONFIRMATION_MESSAGE"}, mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Check support method for confirmation code entity and invalid EmailMessageTypeCode and expect false")
    void supportsForInvalidTypeWithConfirmationCodeEntity_andExpectFalse(EmailMessageTypeCode type) {
        // given
        PlainTextConfirmationCodeEmailMessageCreator creator = ConfirmationCodeEmailMessageCreatorTestingFactory.plainText();
        // when
        boolean actual = creator.supports(type, new ConfirmationCode());
        // then
        assertThat(actual)
                .as("If %s and ConfirmationCode are using then false must be returned", type)
                .isFalse();
    }

    @Test
    @DisplayName("supports for invalid type with non-confirmation code entity and expect false")
    void supportsForInvalidTypeWithNonConfirmationCodeEntity_andExpectFalse() {
        // given
        PlainTextConfirmationCodeEmailMessageCreator creator = ConfirmationCodeEmailMessageCreatorTestingFactory.plainText();
        // when
        boolean actual = creator.supports(MFA_EMAIL_CONFIRMATION_MESSAGE, new Object());
        // then
        assertThat(actual)
                .as("If wrong type and entity are used, then false must be returned!")
                .isFalse();
    }

    @Test
    @DisplayName("Create a message with null confirmation code and expect IllegalArgumentException to be thrown")
    void createMessageWithNull_andExpectException() {
        PlainTextConfirmationCodeEmailMessageCreator creator = ConfirmationCodeEmailMessageCreatorTestingFactory.plainText();
        assertThatThrownBy(() -> creator.createMessage(null, null))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Create a new message with valid confirmation code and expect valid MailMessage to be returned")
    void createMessageWithValidConfCode_andExpectMailMessageToReturn() {
        // Given
        String subject = "Confirmation code is here!";
        PlainTextConfirmationCodeEmailMessageCreator creator = ConfirmationCodeEmailMessageCreatorTestingFactory.plainText();
        ConfirmationCode confirmationCode = ConfirmationCodeFaker.numeric().get();
        EmailReceiver receiver = EmailReceiver.of("odeyalo@gmail.com");
        String expectedBody = "Your confirmation code is: " + confirmationCode.getCode();
        // When
        MailMessage message = creator.createMessage(confirmationCode, receiver);
        // Then
        MailMessageAssert.forMessage(message)
                .content()
                .exactlyEquals(expectedBody)
                .and()
                .subject()
                .exactlyEquals(subject)
                .and()
                .receiver()
                .exactlyEquals(receiver);
    }

    @Test
    @DisplayName("Override subject and expect subject to be overridden")
    void overrideSubject_andExpectSubjectToBeOverridden() {
        // Given
        String subject = "Miku sent you a letter with confirmation code. Check it out!";
        PlainTextConfirmationCodeEmailMessageCreator creator = ConfirmationCodeEmailMessageCreatorTestingFactory.plainText();
        ConfirmationCode confirmationCode = ConfirmationCodeFaker.numeric().get();
        EmailReceiver receiver = EmailReceiver.of("odeyalo@gmail.com");
        String expectedBody = "Your confirmation code is: " + confirmationCode.getCode();

        // when
        creator.overrideSubject(subject);
        MailMessage message = creator.createMessage(confirmationCode, receiver);

        // then
        MailMessageAssert.forMessage(message)
                .subject()
                .exactlyEquals(subject)
                .and()
                .content()
                .exactlyEquals(expectedBody)
                .and()
                .receiver()
                .exactlyEquals(receiver);
    }

    @Test
    @DisplayName("Override body format and expect body to be overridden")
    void overrideBodyFormat_andExpectBodyToBeOverridden() {
        // Given
        String subject = "Confirmation code is here!";
        PlainTextConfirmationCodeEmailMessageCreator creator = ConfirmationCodeEmailMessageCreatorTestingFactory.plainText();
        ConfirmationCode confirmationCode = ConfirmationCodeFaker.numeric().get();
        EmailReceiver receiver = EmailReceiver.of("odeyalo@gmail.com");
        String bodyFormat = "Miku is whispering - \" Your code is: %s \"";
        String expectedBody = String.format(bodyFormat, confirmationCode.getCode());

        // when
        creator.overrideBodyFormat(bodyFormat);
        MailMessage message = creator.createMessage(confirmationCode, receiver);

        // then
        MailMessageAssert.forMessage(message)
                .subject()
                .exactlyEquals(subject)
                .and()
                .content()
                .exactlyEquals(expectedBody)
                .and()
                .receiver()
                .exactlyEquals(receiver);
    }

    @Test
    @DisplayName("Set null to override subject and expect IllegalArgumentException")
    void setNullToSubject_andExpectException() {
        // given
        PlainTextConfirmationCodeEmailMessageCreator creator = ConfirmationCodeEmailMessageCreatorTestingFactory.plainText();
        // then
        assertThatThrownBy(() -> {
            creator.overrideSubject(null);
        });
    }

    @Test
    @DisplayName("Set null to override body and expect IllegalArgumentException")
    void setNullToBody_andExpectException() {
        // given
        PlainTextConfirmationCodeEmailMessageCreator creator = ConfirmationCodeEmailMessageCreatorTestingFactory.plainText();
        // then
        assertThatThrownBy(() -> {
            creator.overrideBodyFormat(null);
        }).isExactlyInstanceOf(IllegalArgumentException.class);
    }
}
