package com.odeyalo.sonata.authentication.testing.contract;

import com.odeyalo.sonata.authentication.AuthenticationApplication;
import com.odeyalo.sonata.authentication.service.password.PasswordContainer;
import com.odeyalo.sonata.authentication.service.password.PasswordUpdatingResult;
import com.odeyalo.sonata.authentication.service.password.SecureUserPasswordUpdater;
import com.odeyalo.sonata.common.shared.ErrorDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@AutoConfigureMessageVerifier
@SpringBootTest(classes = AuthenticationApplication.class, webEnvironment = RANDOM_PORT)
@TestInstance(PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureWireMock(port = 0)
public class PasswordChangeBaseController {

    public static final String INVALID_NEW_PASSWORD = "invalid";
    public static final String VALID_OLD_PASSWORD = "old_password123";
    public static final String INVALID_OLD_PASSWORD = "invalid";
    public static final String VALID_NEW_PASSWORD = "neW_password123";
    @MockBean
    SecureUserPasswordUpdater updater;

    @BeforeEach
    void prepare() {
        PasswordContainer validPasswordContainer = PasswordContainer.of(VALID_OLD_PASSWORD, VALID_NEW_PASSWORD);
        Mockito.when(updater.updatePassword(1, validPasswordContainer)).thenReturn(PasswordUpdatingResult.updated());

        PasswordContainer containerWithInvalidOldPassword = PasswordContainer.of(INVALID_OLD_PASSWORD, VALID_NEW_PASSWORD);
        Mockito.when(updater.updatePassword(1, containerWithInvalidOldPassword)).thenReturn(PasswordUpdatingResult.failed(ErrorDetails.of(
                "old_password_mismatch", "Old password in invalid", "Use correct old password"
        )));

        PasswordContainer containerWithInvalidNewPassword = PasswordContainer.of(VALID_OLD_PASSWORD, INVALID_NEW_PASSWORD);
        Mockito.when(updater.updatePassword(1, containerWithInvalidNewPassword)).thenReturn(PasswordUpdatingResult.failed(ErrorDetails.of(
                "invalid_new_password", "New password pattern is invalid", "Use correct pattern for new password"
        )));

    }
}
