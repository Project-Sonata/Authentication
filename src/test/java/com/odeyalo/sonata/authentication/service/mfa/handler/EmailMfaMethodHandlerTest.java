package com.odeyalo.sonata.authentication.service.mfa.handler;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings;
import com.odeyalo.sonata.authentication.entity.settings.UserSettings;
import com.odeyalo.sonata.authentication.exceptions.UnsupportedMfaMethodException;
import com.odeyalo.sonata.authentication.exceptions.UnsupportedUserMfaTypeException;
import com.odeyalo.sonata.authentication.service.confirmation.*;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeCheckResult;
import com.odeyalo.sonata.authentication.service.mfa.MfaConfirmationSubmission;
import com.odeyalo.sonata.authentication.service.mfa.MfaMethodInfo;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;
import com.odeyalo.sonata.authentication.testing.assertations.MailMessageAssert;
import com.odeyalo.sonata.authentication.testing.factory.ConfirmationCodeManagerTestingFactory;
import com.odeyalo.sonata.authentication.testing.factory.MfaMethodHandlerTestingFactory;
import com.odeyalo.sonata.authentication.testing.faker.UserFaker;
import com.odeyalo.sonata.authentication.testing.spy.MailSenderSpy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;

import java.util.Set;

import static com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings.MfaType.EMAIL;
import static com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings.MfaType.NONE;
import static org.junit.jupiter.api.Assertions.*;

class EmailMfaMethodHandlerTest {
    private static final String SIX_DIGITS_ONLY_REGEX = "^\\D*(\\d\\D*){6}$";

    @Test
    @DisplayName("Start confirmation and expect the message with six digits to be sent")
    void startMfaConfirmation_andExpectMailMessageToBeSent() throws UnsupportedUserMfaTypeException {
        User user = UserFaker.create().get();

        UserMfaSettings emailMfaSettings = new UserMfaSettings(1L, Set.of(EMAIL), user);
        user.setUserSettings(new UserSettings(1L, user, emailMfaSettings));

        MailSenderSpy mailSenderSpy = new MailSenderSpy();
        DelegatingPersistentConfirmationCodeManager manager = ConfirmationCodeManagerTestingFactory
                .createPersistentManagerBuilder()
                .overrideParentGenerator(new NumericConfirmationCodeGenerator())
                .build();

        EmailMfaMethodHandler handler = MfaMethodHandlerTestingFactory
                .emailMfaMethodHandlerBuilder()
                .overrideConfirmationCodeManager(manager)
                .overrideMailSender(mailSenderSpy)
                .build();

        MfaMethodInfo info = handler.startMfaConfirmation(user);

        assertEquals(EmailMfaMethodHandler.MFA_TYPE, info.getType(), String.format("The MFA type must be %s", EmailMfaMethodHandler.MFA_TYPE));
        assertEquals(EmailMfaMethodHandler.CONTENT_MESSAGE, info.getContent(), "The content must be equal");
        assertEquals(user, info.getUser(), "user must be equal to each other!");

        MailMessage sentMessage = mailSenderSpy.getLast();

        MailMessageAssert.forMessage(sentMessage)
                .content()
                .mustContainRegex(SIX_DIGITS_ONLY_REGEX)
                    .and()
                .receiver()
                .exactlyEquals(EmailReceiver.of(user.getEmail()));
    }

    @Test
    @DisplayName("Check valid code and expect true as result")
    void checkValidCodeSubmission_andExpectTrue() {
        // given
        ConfirmationCodeManager manager = Mockito.mock(ConfirmationCodeManager.class);
        String validCode = "123456";
        User user = UserFaker.create().get();

        Mockito.when(manager.verifyCodeAndActive(validCode))
                .thenReturn(ConfirmationCodeCheckResult.valid(user));

        EmailMfaMethodHandler handler = MfaMethodHandlerTestingFactory.emailMfaMethodHandlerBuilder()
                .overrideConfirmationCodeManager(manager)
                .build();
        // when
        boolean submissionResult = handler.checkSubmission(MfaConfirmationSubmission.of(user, validCode));
        // then
        assertTrue(submissionResult, "True must be returned if the code is valid!");
    }


    @Test
    @DisplayName("Check valid code and expect true as result")
    void checkInvalidCodeSubmission_andExpectFalse() {
        // given
        ConfirmationCodeManager manager = Mockito.mock(ConfirmationCodeManager.class);
        String invalidCode = "098765";
        User user = UserFaker.create().get();

        Mockito.when(manager.verifyCodeAndActive(invalidCode))
                .thenReturn(ConfirmationCodeCheckResult.INVALID_CODE);

        EmailMfaMethodHandler handler = MfaMethodHandlerTestingFactory.emailMfaMethodHandlerBuilder()
                .overrideConfirmationCodeManager(manager)
                .build();
        // when
        boolean submissionResult = handler.checkSubmission(MfaConfirmationSubmission.of(user, invalidCode));
        // then
        assertFalse(submissionResult, "False must be returned if the code is invalid!");
    }

    @Test
    @DisplayName("Use unsupported mfa type and expect exception")
    void unsupportedMfaType_andExpectException() {
        EmailMfaMethodHandler handler = MfaMethodHandlerTestingFactory.emailMfaMethodHandler();
        User user = UserFaker.create().get();
        UserMfaSettings noneMfaSettings = new UserMfaSettings(1L, Set.of(NONE), user);
        user.setUserSettings(new UserSettings(1L, user, noneMfaSettings));

        assertThrows(UnsupportedUserMfaTypeException.class, () -> handler.startMfaConfirmation(user));
    }

    @Test
    @DisplayName("Assert that handler support valid mfa type")
    void supportedMfaType() {
        EmailMfaMethodHandler handler = MfaMethodHandlerTestingFactory.emailMfaMethodHandler();
        assertEquals(EMAIL, handler.supportedMfaType());
    }

    @ParameterizedTest
    @EnumSource(value = UserMfaSettings.MfaType.class, names = "EMAIL", mode = EnumSource.Mode.EXCLUDE)
    @DisplayName("Assert that handler support valid mfa type")
    void useUnsupportedTypes_andExpectTypeToBeFalse(UserMfaSettings.MfaType type) {
        EmailMfaMethodHandler handler = MfaMethodHandlerTestingFactory.emailMfaMethodHandler();
        assertNotEquals(type, handler.supportedMfaType(), "The values must be not equal!");
    }
}
