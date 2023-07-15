package com.odeyalo.sonata.authentication.controller;

import com.odeyalo.sonata.authentication.service.password.PasswordContainer;
import com.odeyalo.sonata.authentication.service.password.PasswordUpdatingResult;
import com.odeyalo.sonata.authentication.service.password.SecureUserPasswordUpdater;
import com.odeyalo.sonata.common.authentication.dto.request.PasswordContainerDto;
import com.odeyalo.sonata.common.authentication.dto.response.PasswordUpdatingResultDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth/settings")
@RestController
public class UserSettingsController {
    private final SecureUserPasswordUpdater secureUserPasswordUpdater;

    public UserSettingsController(SecureUserPasswordUpdater secureUserPasswordUpdater) {
        this.secureUserPasswordUpdater = secureUserPasswordUpdater;
    }

    /**
     * Update the user's password if and only if old password is valid
     * Since this microservice will not be accessible from the outside and used only as user credentials store, there is no need to check the access token and so on.
     * @param userId - user id to update password to
     * @param container - container with passwords
     * @return - response
     */
    @PutMapping("/password-change")
    public ResponseEntity<?> changePassword(@RequestParam long userId, @RequestBody PasswordContainerDto container) {
        PasswordUpdatingResult result = secureUserPasswordUpdater.updatePassword(userId,  PasswordContainer.of(container.getOldPassword(), container.getNewPassword()));
        PasswordUpdatingResultDto dto = PasswordUpdatingResultDto.of(result.isUpdated(), result.getErrorDetails());
        return ResponseEntity.status(result.isUpdated() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(dto);
    }
}
