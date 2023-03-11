package com.odeyalo.sonata.authentication.service.registration;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.authentication.testing.factory.UserRegistrationServiceTestingFactory;
import com.odeyalo.sonata.authentication.testing.spy.DelegatingEmailConfirmationCodeGeneratorSenderSpy;
import com.odeyalo.sonata.authentication.testing.spy.EmptyEmailConfirmationCodeGeneratorSenderSpy;
import com.odeyalo.sonata.authentication.testing.stubs.ThrowableUserRepositoryStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
// SavingConfirmationCodeGenerator - generate and save the confirmation code in some kind of storage
// EmailConfirmationManager - manager that create, validate and delete confirmation codes and activate the user if code is valid
// EmailConfirmationCodeGeneratorSender - Facade service to  generate and send confirmation code to email
// ConfirmationCodeGenerator - generate the confirmation code
// MailSender - sender that used to send the message to the email
class EmailConfirmationUserRegistrationServiceTest {

    @Test
    @DisplayName("Register the user with valid info and expect success as result")
    void registerUserWithValidInfo_andExpectSuccess() {
        // Given
        String email = "odeyalo@gmail.com";
        String password = "password123";

        UserRegistrationServiceTestingFactory.DefaultUserRegistrationServiceBuilder builder = UserRegistrationServiceTestingFactory
                .createDefaultService();

        EmptyEmailConfirmationCodeGeneratorSenderSpy senderSpy = new EmptyEmailConfirmationCodeGeneratorSenderSpy();

        UserRepository userRepositorySpy = builder.getUserRepository();
        PasswordEncoder passwordEncoderSpy = builder.getPasswordEncoder();

        EmailConfirmationUserRegistrationService userRegistrationService = builder
                .overrideEmailConfirmationCodeGeneratorSender(senderSpy)
                .build();

        UserRegistrationInfo info = new UserRegistrationInfo(email, password,
                "MALE", LocalDate.of(2000, 12, 12), false);
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
        String email = "odeyalo@gmail.com";
        String password = "password123";

        // Given
        UserRepository userRepository = new ThrowableUserRepositoryStub();
        EmailConfirmationUserRegistrationService registrationService = UserRegistrationServiceTestingFactory.createDefaultService()
                .overrideUserRepository(userRepository)
                .build();


        UserRegistrationInfo info = new UserRegistrationInfo(email, password,
                "MALE", LocalDate.of(2000, 12, 12), false);
        // When
        RegistrationResult registrationResult = registrationService.registerUser(info);
        // Then
        assertFalse(registrationResult.success(), "If any error was occurred during saving, then false must be returned!");
        assertEquals(RegistrationResult.RequiredAction.DO_NOTHING, registrationResult.action(), "RequiredAction.DO_NOTHING must be returned!");
        assertEquals(ErrorDetails.SERVER_ERROR, registrationResult.errorDetails(), "ErrorDetails.SERVER_ERROR must be returned if the saving operation cannot be performed because repository throws an exception!");
    }
}
