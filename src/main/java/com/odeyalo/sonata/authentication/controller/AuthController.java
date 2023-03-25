package com.odeyalo.sonata.authentication.controller;

import com.odeyalo.sonata.authentication.dto.UserInfo;
import com.odeyalo.sonata.authentication.dto.error.ApiErrorDetailsInfo;
import com.odeyalo.sonata.authentication.dto.request.ConfirmationCodeRequestDto;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.dto.response.EmailConfirmationStatusResponseDto;
import com.odeyalo.sonata.authentication.dto.response.UserRegistrationConfirmationResponseDto;
import com.odeyalo.sonata.authentication.service.confirmation.EmailConfirmationManager;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeCheckResult;
import com.odeyalo.sonata.authentication.service.registration.RegistrationResult;
import com.odeyalo.sonata.authentication.service.registration.UserRegistrationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/auth")
public class AuthController {
    public static final String INVALID_CONFIRMATION_CODE_MESSAGE = "The code is invalid";
    public static final String CONFIRMATION_URL_REL = "confirmation_url";
    public static final String CONFIRMATION_CODE_WAS_SENT_MESSAGE = "We sent confirmation letter to your email. Check it out";
    private final UserRegistrationManager userRegistrationManager;
    private final EmailConfirmationManager manager;

    @Autowired
    public AuthController(UserRegistrationManager userRegistrationManager, EmailConfirmationManager manager) {
        this.userRegistrationManager = userRegistrationManager;
        this.manager = manager;
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationInfo info) {
        RegistrationResult result = userRegistrationManager.registerUser(info);

        if (!result.success()) {
            return ResponseEntity.badRequest().body(new ApiErrorDetailsInfo(HttpStatus.BAD_REQUEST, result.errorDetails()));
        }

        UserRegistrationConfirmationResponseDto dto = new UserRegistrationConfirmationResponseDto(CONFIRMATION_CODE_WAS_SENT_MESSAGE);
        return getSuccessResponse(info, dto);
    }

    @PostMapping(value = "/confirm/email", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> confirmEmail(@RequestBody ConfirmationCodeRequestDto codeDto) {
        ConfirmationCodeCheckResult result = manager.verifyCode(codeDto.getCodeValue());

        if (!result.isValid()) {
            EmailConfirmationStatusResponseDto dto = EmailConfirmationStatusResponseDto.confirmationFailed(INVALID_CONFIRMATION_CODE_MESSAGE);
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(dto);
        }
        UserInfo userInfo = UserInfo.from(result.getUser());
        EmailConfirmationStatusResponseDto dto = EmailConfirmationStatusResponseDto.confirmationSuccess(userInfo);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(dto);
    }

    private ResponseEntity<UserRegistrationConfirmationResponseDto> getSuccessResponse(UserRegistrationInfo info, UserRegistrationConfirmationResponseDto dto) {
        Link link = linkTo(
                methodOn(AuthController.class).confirmEmail(null))
                .withRel(CONFIRMATION_URL_REL);

        Link selfRel = linkTo(WebMvcLinkBuilder
                .methodOn(AuthController.class)
                .registerUser(info))
                .withSelfRel();
        dto.add(link, selfRel);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
