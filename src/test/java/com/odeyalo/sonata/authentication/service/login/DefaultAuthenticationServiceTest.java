package com.odeyalo.sonata.authentication.service.login;

import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.common.LoginCredentials;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings;
import com.odeyalo.sonata.authentication.entity.settings.UserSettings;
import com.odeyalo.sonata.authentication.testing.factory.AuthenticationManagerTestingFactory;
import com.odeyalo.sonata.authentication.testing.faker.UserFaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link DefaultAuthenticationService}
 */
class DefaultAuthenticationServiceTest {

    @Test
    @DisplayName("Authenticate the existing user and expect success as result")
    void authenticateExistingUser_andExpectSuccess() {
        // given
        User user = UserFaker.create().makeActive().get();

        LoginCredentials credentials = LoginCredentials.of(user.getEmail(), user.getPassword());

        AuthenticationManagerTestingFactory.DefaultAuthenticationManagerBuilder builder = AuthenticationManagerTestingFactory.createDefaultBuilder();

        PasswordEncoder passwordEncoder = builder.getPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        DefaultAuthenticationService authenticationManager = builder.withPredefinedUsers(user).build();
        // when
        AuthenticationResult result = authenticationManager.authenticate(credentials);
        // then
        assertThat(result.isSuccess())
                .as("If credentials are correct, then AuthenticationResult#success must be returned!")
                .isTrue();

        assertThat(user)
                .as("The user which was authenticated should be properly returned!")
                .isEqualTo(result.getUser());

        assertThat(result.getErrorDetails())
                .as("If user has been successfully authenticated, then ErrorDetails must be null")
                .isNull();

        assertEquals(AuthenticationResult.Type.LOGIN_COMPLETED, result.getType(), "If credentials are correct, then LOGIN_COMPLETED must be returned");
    }

    @Test
    @DisplayName("Authenticate the existing user but with wrong password and expect failed as result")
    void authenticateExistingUserWithWrongPassword_andExpectFailed() {
        // given
        String wrongPassword = "AyanakodjiKiotaka666";
        User user = UserFaker.create().makeActive().get();

        LoginCredentials incorrectCredentials = LoginCredentials.of(user.getEmail(), wrongPassword);

        AuthenticationManagerTestingFactory.DefaultAuthenticationManagerBuilder builder = AuthenticationManagerTestingFactory.createDefaultBuilder();

        PasswordEncoder passwordEncoder = builder.getPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        DefaultAuthenticationService authenticationManager = builder
                .withPredefinedUsers(user)
                .build();
        // when
        AuthenticationResult result = authenticationManager.authenticate(incorrectCredentials);

        // then
        assertThat(result.isSuccess())
                .as("If the credentials are wrong, then 'failed' should be returned!")
                .isFalse();

        assertThat(result.getUser())
                .as("If the result is 'failed', then user must be null!")
                .isNull();

        assertEquals(AuthenticationResult.Type.FAILED, result.getType(), "If password is not correct, then FAILED should be returned");

        assertEquals(AuthenticationResult.PossibleErrors.INVALID_CREDENTIALS, result.getErrorDetails(), "If the invalid credentials were provided, then AuthenticationResult.PossibleErrors.INVALID_CREDENTIALS error should be returned");

    }

    @Test
    @DisplayName("Authenticate the non-existing user and expect failed as result")
    void authenticateNonExistingUser_andExpectFailed() {
        // given
        String email = "odeyalo@gmail.com";
        String password = "odeyal0lovesMikuNakano";

        LoginCredentials incorrectCredentials = LoginCredentials.of(email, password);

        DefaultAuthenticationService authenticationManager = AuthenticationManagerTestingFactory.createDefault();

        // when
        AuthenticationResult result = authenticationManager.authenticate(incorrectCredentials);

        // then
        assertThat(result.isSuccess())
                .as("If the credentials are wrong, then 'failed' should be returned!")
                .isFalse();

        assertThat(result.getUser())
                .as("If the result is 'failed', then user must be null!")
                .isNull();

        assertEquals(AuthenticationResult.Type.FAILED, result.getType(), "If credentials are wrong, then FAILED should be returned");

        assertEquals(AuthenticationResult.PossibleErrors.INVALID_CREDENTIALS, result.getErrorDetails(), "If the invalid credentials were provided, then AuthenticationResult.PossibleErrors.INVALID_CREDENTIALS error should be returned");

    }

    @Test
    @DisplayName("Authenticate the existing user but non-activated user and expect failed as result")
    void authenticateExistingNonActivatedUser_andExpectFailed() {
        // given
        User user = UserFaker.create().makeInactive().get();

        LoginCredentials credentials = LoginCredentials.of(user.getEmail(), user.getPassword());

        AuthenticationManagerTestingFactory.DefaultAuthenticationManagerBuilder builder = AuthenticationManagerTestingFactory.createDefaultBuilder();

        PasswordEncoder passwordEncoder = builder.getPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        DefaultAuthenticationService authenticationManager = builder.withPredefinedUsers(user).build();
        // when
        AuthenticationResult result = authenticationManager.authenticate(credentials);
        // then
        assertThat(result.isSuccess())
                .as("If the credentials are correct but user is not activated, then 'failed' should be returned!")
                .isFalse();

        assertThat(result.getUser())
                .as("If the result is 'failed', then user must be null!")
                .isNull();

        assertEquals(AuthenticationResult.Type.FAILED, result.getType(), "If email is not activated, then FAILED should be returned");

        assertEquals(AuthenticationResult.PossibleErrors.EMAIL_CONFIRMATION_REQUIRED, result.getErrorDetails(), "If the user exist but not activated, then AuthenticationResult.PossibleErrors.EMAIL_CONFIRMATION_REQUIRED error should be returned");
    }

    @Test
    @DisplayName("Authenticate the user with enabled MFA and expect pending_mfa status as result")
    void authenticateSecuredUser_andExpectPendingMfaStatus() {
        // given
        User user = UserFaker.create().makeActive().get();
        UserSettings userSettings = UserSettings.empty(user);
        UserMfaSettings mfaSettings = UserMfaSettings.empty(user);
        mfaSettings.addAuthorizedMfaType(UserMfaSettings.MfaType.TOTP);
        userSettings.setUserMfaSettings(mfaSettings);
        user.setUserSettings(userSettings);

        LoginCredentials validCredentials = LoginCredentials.of(user.getEmail(), user.getPassword());

        AuthenticationManagerTestingFactory.DefaultAuthenticationManagerBuilder builder = AuthenticationManagerTestingFactory.createDefaultBuilder();

        PasswordEncoder passwordEncoder = builder.getPasswordEncoder();

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        DefaultAuthenticationService authenticationManager = builder
                .withPredefinedUsers(user)
                .build();

        // when
        AuthenticationResult result = authenticationManager.authenticate(validCredentials);
        // then
        assertThat(result.getErrorDetails())
                .as("ErrorDetails should be null if no error happened")
                .isNull();
        assertThat(result.getType())
                .as("If MFA is enabled and credentials are correct, then PENDING_MFA must be returned")
                .isEqualTo(AuthenticationResult.Type.PENDING_MFA);
        assertThat(result.getUser())
                .as("If authenticate was success, then exactly the same user should be returned")
                .isEqualTo(user);
    }
}
