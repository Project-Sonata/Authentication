package com.odeyalo.sonata.authentication.service.registration;

import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.testing.factory.UserRegistrationManagerTestingFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DefaultUserRegistrationManagerTest {

    @Test
    @DisplayName("Register the user with valid info and expect user to be registered and RequiredAction.CONFIRM_EMAIL as result")
    void registerUserWithValidRegistrationInfo_andExpectSuccess() {
        DefaultUserRegistrationManager defaultUserRegistrationManager = UserRegistrationManagerTestingFactory.createDefault();

        String email = "odeyalo@gmail.com";

        UserRegistrationInfo info = new UserRegistrationInfo(email, "password123",
                "MALE", LocalDate.of(2000, 12, 12), false);

        RegistrationResult result = defaultUserRegistrationManager.registerUser(info);

        assertTrue(result.success());
        assertEquals(RegistrationResult.RequiredAction.CONFIRM_EMAIL,result.action());
    }

    @Test
    @DisplayName("Register the user with invalid info and expect user to be rejected")
    void registerUserWithInvalidRegistrationInfo_andExpectError() {
        DefaultUserRegistrationManager defaultUserRegistrationManager = UserRegistrationManagerTestingFactory
                .createDefault();

        String email = "odeyalo@gmail.com";

        UserRegistrationInfo info = new UserRegistrationInfo(email, "password",
                "MALE", LocalDate.of(2000, 12, 12), false);

        RegistrationResult result = defaultUserRegistrationManager.registerUser(info);

        assertFalse(result.success());
    }
}
