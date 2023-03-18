package com.odeyalo.sonata.authentication.service.registration;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.authentication.testing.factory.UserRegistrationManagerTestingFactory;
import com.odeyalo.sonata.authentication.testing.factory.UserRegistrationServiceTestingFactory;
import com.odeyalo.sonata.authentication.testing.factory.UserRepositoryTestingFactory;
import com.odeyalo.sonata.authentication.testing.faker.UserRegistrationInfoFaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultUserRegistrationManagerTest {

    @Test
    @DisplayName("Register the user with valid info and expect user to be registered and RequiredAction.CONFIRM_EMAIL as result")
    void registerUserWithValidRegistrationInfo_andExpectSuccess() {
        // given
        UserRepository spy = UserRepositoryTestingFactory.create();
        EmailConfirmationUserRegistrationService service = UserRegistrationServiceTestingFactory.createEmailService().overrideUserRepository(spy).build();

        DefaultUserRegistrationManager defaultUserRegistrationManager = UserRegistrationManagerTestingFactory
                .builderDefault()
                .overrideRegistrationService(service)
                .build();

        UserRegistrationInfo info = UserRegistrationInfoFaker.create().get();
        // When
        RegistrationResult result = defaultUserRegistrationManager.registerUser(info);

        // Then
        assertTrue(result.success(), "If user has been registered, then 'success' flag must be true!");
        assertEquals(RegistrationResult.RequiredAction.CONFIRM_EMAIL,result.action(), "If the EmailConfirmationUserRegistrationService is being used, the RequiredAction.CONFIRM_EMAIL must be returned");

        User user = spy.findUserByEmail(info.getEmail());

        assertEquals(info.getEmail(), user.getEmail(), "Provided user by user must be saved in plain text!");
        assertNotNull(user.getPassword(), "Password must be encoded and saved");
        assertNotEquals(info.getPassword(), user.getPassword(), "The user's password must be encoded and MUST NOT be saved in plain text");
        assertFalse(user.isActive(), "If RegistrationResult returns RequiredAction.CONFIRM_EMAIL, then user must be activated ONLY AFTER email confirmation");
    }

    @Test
    @DisplayName("Register the user with invalid password format and expect user to be rejected with invalid email password")
    void registerUserWithInvalidRegistrationInfo_andExpectError() {
        DefaultUserRegistrationManager defaultUserRegistrationManager = UserRegistrationManagerTestingFactory
                .createDefault();
        String invalidPassword = "odeyaloissad";

        UserRegistrationInfo info = UserRegistrationInfoFaker.create()
                .overridePassword(invalidPassword)
                .get();

        RegistrationResult result = defaultUserRegistrationManager.registerUser(info);

        assertFalse(result.success());
        assertEquals(ErrorDetails.INVALID_PASSWORD, result.errorDetails());
    }

    @Test
    @DisplayName("Set null and expect IllegalArgumentException to be thrown")
    void testNullAsParam_andExpectException() {
        // given
        DefaultUserRegistrationManager defaultUserRegistrationManager = UserRegistrationManagerTestingFactory
                .createDefault();
        // Then
        assertThrows(IllegalArgumentException.class, () -> {
            defaultUserRegistrationManager.registerUser(null);
        });
    }
}
