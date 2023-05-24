package com.odeyalo.sonata.authentication.service.registration;

import com.odeyalo.sonata.authentication.common.ExtendedErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.authentication.testing.factory.UserRegistrationServiceTestingFactory;
import com.odeyalo.sonata.authentication.testing.faker.UserRegistrationInfoFaker;
import com.odeyalo.sonata.authentication.testing.spy.EmptyEmailConfirmationCodeGeneratorSenderSpy;
import com.odeyalo.sonata.authentication.testing.stubs.ThrowableEmailConfirmationCodeGeneratorSenderStub;
import com.odeyalo.sonata.authentication.testing.stubs.ThrowableUserRepositoryStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link EmailConfirmationUserRegistrationService}
 */
class EmailConfirmationUserRegistrationServiceTest {

    @Test
    @DisplayName("Register the user with valid info and expect success as result")
    void registerUserWithValidInfo_andExpectSuccess() {
        // Given
        String email = "odeyalo@gmail.com";
        String password = "password123";

        UserRegistrationServiceTestingFactory.EmailConfirmationUserRegistrationServiceBuilder builder = UserRegistrationServiceTestingFactory
                .createEmailService();

        EmptyEmailConfirmationCodeGeneratorSenderSpy senderSpy = new EmptyEmailConfirmationCodeGeneratorSenderSpy();

        UserRepository userRepositorySpy = builder.getUserRepository();
        PasswordEncoder passwordEncoderSpy = builder.getPasswordEncoder();

        EmailConfirmationUserRegistrationService userRegistrationService = builder
                .overrideEmailConfirmationCodeGeneratorSender(senderSpy)
                .build();

        AdvancedUserRegistrationInfo info = UserRegistrationInfoFaker.create()
                .overrideEmail(email)
                .overridePassword(password)
                .get();

        // When
        RegistrationResult result = userRegistrationService.registerUser(info);
        // Then
        assertTrue(result.success(), "If the user data is valid, then user should be registered and 'success' field should be true");
        assertEquals(RegistrationResult.RequiredAction.CONFIRM_EMAIL, result.action(),
                "If the user enter email and use default registration form, then RequiredAction.CONFIRM_EMAIL should be returned");
        User actualUser = userRepositorySpy.findUserByEmail(email);

        assertNotNull(actualUser, "If the user has been registered, then user should be added to DB or other type of storage");
        assertFalse(actualUser.isActive(), "If required action is CONFIRM_EMAIL, then user should be activated only after email confirmation");
        assertEquals(email, actualUser.getEmail(), "Email must be saved!");
        assertTrue(passwordEncoderSpy.matches(password, actualUser.getPassword()), "Raw password must be match encoded!");

        assertTrue(senderSpy.wasSent(), "EmailConfirmationUserRegistrationService must send the confirmation code to the email!");
    }

    @Test
    @DisplayName("Register user with occurred error and expect RegistrationResult.failed()")
    void registerUserWithException_andExpectFailed() {
        // Given
        UserRepository userRepository = new ThrowableUserRepositoryStub();
        EmailConfirmationUserRegistrationService registrationService = UserRegistrationServiceTestingFactory.createEmailService()
                .overrideUserRepository(userRepository)
                .build();

        AdvancedUserRegistrationInfo info = UserRegistrationInfoFaker.create().get();
        // When
        RegistrationResult registrationResult = registrationService.registerUser(info);
        // Then
        assertFalse(registrationResult.success(), "If any error was occurred during saving, then false must be returned!");
        assertEquals(RegistrationResult.RequiredAction.DO_NOTHING, registrationResult.action(), "RequiredAction.DO_NOTHING must be returned!");
        assertEquals(ExtendedErrorDetails.SERVER_ERROR, registrationResult.errorDetails(), "ErrorDetails.SERVER_ERROR must be returned if the saving operation cannot be performed because repository throws an exception!");
    }


    @Test
    @DisplayName("Confirmation message sending is failed, then expect user not be registered and RegistrationException to be thrown")
    void messageSendingFailed_andExpectExceptionAndUserNotRegistered() {
        // given
        ThrowableEmailConfirmationCodeGeneratorSenderStub stub = new ThrowableEmailConfirmationCodeGeneratorSenderStub();

        UserRegistrationServiceTestingFactory.EmailConfirmationUserRegistrationServiceBuilder builder = UserRegistrationServiceTestingFactory.createEmailService()
                .overrideEmailConfirmationCodeGeneratorSender(stub);

        UserRepository repo = builder.getUserRepository();
        EmailConfirmationUserRegistrationService registrationService = builder.build();

        AdvancedUserRegistrationInfo info = UserRegistrationInfoFaker.create().get();

        // when
        RegistrationResult registrationResult = registrationService.registerUser(info);
        // then
        assertFalse(registrationResult.success(), "Registration result must be false, since exception was thrown");
        assertEquals(RegistrationResult.RequiredAction.DO_NOTHING, registrationResult.action(), "RequiredAction.DO_NOTHING must be returned if error was occurred on server side");
        assertEquals(ExtendedErrorDetails.SERVER_ERROR, registrationResult.errorDetails(), "SERVER_ERROR must be returned if application is unable to send the email message");

        User user = repo.findUserByEmail(info.getEmail());

        assertNull(user, "User must be not saved if error was occurred!");
    }
}
