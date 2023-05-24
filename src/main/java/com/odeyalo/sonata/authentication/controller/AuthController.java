package com.odeyalo.sonata.authentication.controller;

import com.odeyalo.sonata.authentication.common.LoginCredentials;
import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.controller.support.DataRequestAssociationService;
import com.odeyalo.sonata.authentication.dto.ExtendedUserInfo;
import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;
import com.odeyalo.sonata.authentication.dto.response.ExtendedAuthenticationResultResponse;
import com.odeyalo.sonata.authentication.dto.response.EmailConfirmationStatusResponseDto;
import com.odeyalo.sonata.authentication.service.confirmation.EmailConfirmationManager;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeCheckResult;
import com.odeyalo.sonata.authentication.service.login.AuthenticationManager;
import com.odeyalo.sonata.authentication.service.registration.RegistrationResult;
import com.odeyalo.sonata.authentication.service.registration.UserRegistrationManager;
import com.odeyalo.sonata.common.authentication.dto.request.ConfirmationCodeRequestDto;
import com.odeyalo.sonata.common.authentication.dto.response.UserRegistrationConfirmationResponseDto;
import com.odeyalo.sonata.common.shared.ApiErrorDetailsInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import static com.odeyalo.sonata.authentication.common.AuthenticationResult.Type.LOGIN_COMPLETED;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/auth")
public class AuthController {
    public static final String INVALID_CONFIRMATION_CODE_MESSAGE = "The code is invalid";
    public static final String CONFIRMATION_URL_REL = "confirmation_url";
    public static final String CONFIRMATION_CODE_WAS_SENT_MESSAGE = "We sent confirmation letter to your email. Check it out";
    public static final String LOGIN_ASSOCIATED_USER_KEY = "login_associated_user_key";
    private final UserRegistrationManager userRegistrationManager;
    private final EmailConfirmationManager emailConfirmationManager;
    private final AuthenticationManager authenticationManager;
    private final DataRequestAssociationService dataRequestAssociationService;

    @Autowired
    public AuthController(UserRegistrationManager userRegistrationManager, EmailConfirmationManager emailConfirmationManager, AuthenticationManager authenticationManager, DataRequestAssociationService dataRequestAssociationService) {
        this.userRegistrationManager = userRegistrationManager;
        this.emailConfirmationManager = emailConfirmationManager;
        this.authenticationManager = authenticationManager;
        this.dataRequestAssociationService = dataRequestAssociationService;
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody AdvancedUserRegistrationInfo info) {
        RegistrationResult result = userRegistrationManager.registerUser(info);

        if (!result.success()) {
            return ResponseEntity.badRequest().body(new ApiErrorDetailsInfo(HttpStatus.BAD_REQUEST, result.errorDetails()));
        }

        UserRegistrationConfirmationResponseDto dto = new UserRegistrationConfirmationResponseDto(CONFIRMATION_CODE_WAS_SENT_MESSAGE);
        return getSuccessResponse(info, dto);
    }

    @PostMapping(value = "/confirm/email", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmailConfirmationStatusResponseDto> confirmEmail(@RequestBody ConfirmationCodeRequestDto codeDto) {
        ConfirmationCodeCheckResult result = emailConfirmationManager.verifyCode(codeDto.getCodeValue());

        if (!result.isValid()) {
            EmailConfirmationStatusResponseDto dto = EmailConfirmationStatusResponseDto.confirmationFailed(INVALID_CONFIRMATION_CODE_MESSAGE);
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(dto);
        }
        ExtendedUserInfo userInfo = ExtendedUserInfo.from(result.getUser());
        EmailConfirmationStatusResponseDto dto = EmailConfirmationStatusResponseDto.confirmationSuccess(userInfo);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(dto);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ExtendedAuthenticationResultResponse> loginUser(@RequestBody LoginCredentials credentials,
                                                                          HttpServletRequest request,
                                                                          HttpServletResponse response) {
        AuthenticationResult result = authenticationManager.authenticate(credentials);
        ExtendedAuthenticationResultResponse body = ExtendedAuthenticationResultResponse.from(result);

        associateIfNecessary(request, response, result);

        return ResponseEntity.status(body.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    private void associateIfNecessary(HttpServletRequest request, HttpServletResponse response, AuthenticationResult result) {
        if (result.getType() != LOGIN_COMPLETED) {
            dataRequestAssociationService.associateData(LOGIN_ASSOCIATED_USER_KEY, result.getUser(), request, response);
        }
    }

    private ResponseEntity<UserRegistrationConfirmationResponseDto> getSuccessResponse(AdvancedUserRegistrationInfo info, UserRegistrationConfirmationResponseDto dto) {
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
