package com.odeyalo.sonata.authentication.service.registration;

import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.InMemoryUserRepository;
import com.odeyalo.sonata.authentication.testing.factory.UserRegistrationServiceTestingFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DefaultUserRegistrationServiceTest {

    @Test
    @DisplayName("Register the user with valid info and expect success as result")
    void registerUserWithValidInfo_andExpectSuccess() {
        // Given
        InMemoryUserRepository spy = new InMemoryUserRepository();
        String email = "odeyalo@gmail.com";

        DefaultUserRegistrationService userRegistrationService = UserRegistrationServiceTestingFactory
                .createDefaultService()
                .overrideUserRepository(spy)
                .build();

        UserRegistrationInfo info = new UserRegistrationInfo(email, "password",
                "MALE", LocalDate.of(2000, 12, 12), false);
        // When
        RegistrationResult result = userRegistrationService.registerUser(info);
        // Then
        assertTrue(result.success(), "If the user data is valid, then user should be registered and 'success' field should be true");
        assertEquals(RegistrationResult.RequiredAction.CONFIRM_EMAIL, result.action(),
                "If the user enter email and use default registration form, then RequiredAction.CONFIRM_EMAIL should be returned");
        User actualUser = spy.findUserByEmail(email);
        assertNotNull(actualUser, "If the user has been registered, then user should be added to DB or other type of storage");
        assertFalse(actualUser.isActive(), "If required action is CONFIRM_EMAIL, then user should be activated only after email confirmation");
    }
}
