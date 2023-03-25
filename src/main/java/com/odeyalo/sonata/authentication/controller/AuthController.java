package com.odeyalo.sonata.authentication.controller;

import com.odeyalo.sonata.authentication.dto.UserInfo;
import com.odeyalo.sonata.authentication.dto.error.ApiErrorDetailsInfo;
import com.odeyalo.sonata.authentication.dto.request.ConfirmationCodeRequestDto;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.dto.response.EmailConfirmationStatusResponseDto;
import com.odeyalo.sonata.authentication.dto.response.TokensResponse;
import com.odeyalo.sonata.authentication.dto.response.UserRegistrationConfirmationResponseDto;
import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.repository.ConfirmationCodeRepository;
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
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRegistrationManager userRegistrationManager;
    private final EmailConfirmationManager manager;

    @Autowired
    public AuthController(UserRegistrationManager userRegistrationManager, EmailConfirmationManager manager) {
        this.userRegistrationManager = userRegistrationManager;
        this.manager = manager;
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationInfo info) {
        UserRegistrationConfirmationResponseDto dto = new UserRegistrationConfirmationResponseDto("We sent confirmation letter to your email. Check it out");
        RegistrationResult result = userRegistrationManager.registerUser(info);

        if (!result.success()) {
            return ResponseEntity.badRequest().body(new ApiErrorDetailsInfo(HttpStatus.BAD_REQUEST, result.errorDetails()));
        }
        return getSuccessResponse(info, dto);
    }
    // user confirmed email -> token and refresh token must be returned
    // controller -> email confirmation manager.verify
    @PostMapping(value = "/confirm/email", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> confirmEmail(@RequestBody ConfirmationCodeRequestDto codeDto) {
        ConfirmationCodeCheckResult result = manager.verifyCode(codeDto.getCodeValue());

        if (!result.isValid()) {
            EmailConfirmationStatusResponseDto dto = EmailConfirmationStatusResponseDto.confirmationFailed("The code is invalid");
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(dto);
        }
        EmailConfirmationStatusResponseDto dto = EmailConfirmationStatusResponseDto.confirmationSuccess(
                UserInfo.from(result.getUser()));

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(dto);
    }

    //todo
    private ResponseEntity<UserRegistrationConfirmationResponseDto> getSuccessResponse(UserRegistrationInfo info, UserRegistrationConfirmationResponseDto dto) {
        Link link = linkTo(methodOn(AuthController.class).confirmEmail(null)).withRel("confirmation_url");
        Link selfRel = getSelfRel(info);

        dto.add(link);
        dto.add(selfRel);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private static Link getSelfRel(UserRegistrationInfo info) {
        return linkTo(WebMvcLinkBuilder.methodOn(AuthController.class).registerUser(info)).withSelfRel();
    }
}
