package com.odeyalo.sonata.authentication.controller;

import com.odeyalo.sonata.authentication.controller.support.DataRequestAssociationService;
import com.odeyalo.sonata.authentication.dto.request.ConfirmationCodeData;
import com.odeyalo.sonata.authentication.dto.response.GenericMfaAuthenticationMethodInfoResponse;
import com.odeyalo.sonata.authentication.dto.response.MfaConfirmationSubmissionResultResponse;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.exceptions.MalformedLoginSessionException;
import com.odeyalo.sonata.authentication.exceptions.MissingConfirmationCodeValueException;
import com.odeyalo.sonata.authentication.exceptions.UnsupportedMfaMethodException;
import com.odeyalo.sonata.authentication.service.mfa.MfaConfirmationSubmission;
import com.odeyalo.sonata.authentication.service.mfa.MfaMethodInfo;
import com.odeyalo.sonata.authentication.service.mfa.handler.MfaMethodHandler;
import com.odeyalo.sonata.authentication.service.mfa.handler.MfaMethodHandlerFactory;
import com.odeyalo.sonata.authentication.support.converter.dto.EmailMfaMethodInfoConverter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Expose endpoints for MFA login and confirmation
 */
@RestController
@RequestMapping("/mfa")
public class MfaController {
    private final MfaMethodHandlerFactory handlerFactory;
    private final DataRequestAssociationService requestAssociationService;
    private final EmailMfaMethodInfoConverter emailMfaMethodInfoConverter;
    private final Logger logger = LoggerFactory.getLogger(MfaController.class);
    public static final String MFA_METHOD_KEY = "mfa_method";

    @Autowired
    public MfaController(MfaMethodHandlerFactory handlerFactory, DataRequestAssociationService requestAssociationService, EmailMfaMethodInfoConverter emailMfaMethodInfoConverter) {
        this.handlerFactory = handlerFactory;
        this.requestAssociationService = requestAssociationService;
        this.emailMfaMethodInfoConverter = emailMfaMethodInfoConverter;
    }


    @PostMapping("/login")
    public ResponseEntity<?> startMfaProcess(@RequestParam(name = "method") String method,
                                             HttpServletRequest request,
                                             HttpServletResponse response) throws Exception {
        MfaMethodHandler handler = handlerFactory.getHandler(method);
        if (handler == null) {
            throw new UnsupportedMfaMethodException("Method does not supported.", method);
        }
        User user = (User) requestAssociationService.get(request, AuthController.LOGIN_ASSOCIATED_USER_KEY);
        if (user == null) {
            throw new MalformedLoginSessionException("The session is malformed and MFA authentication cannot be performed");
        }
        requestAssociationService.associateData(MFA_METHOD_KEY, method, request, response);
        logger.info("Starting the MFA authentication with method: {} and for user: {}", method, user.getId());

        MfaMethodInfo mfaMethodInfo = handler.startMfaConfirmation(user);
        GenericMfaAuthenticationMethodInfoResponse authenticationResponse = emailMfaMethodInfoConverter.convert(mfaMethodInfo);
        return ResponseEntity.accepted().body(authenticationResponse);
    }

    @PostMapping("/login/check")
    public ResponseEntity<?> checkMfaConfirmation(HttpServletRequest request, @RequestBody ConfirmationCodeData data) throws MalformedLoginSessionException {
        if (data == null || data.getCode() == null) {
            throw new MissingConfirmationCodeValueException("The confirmation code must be presented!");
        }
        User user = (User) requestAssociationService.get(request, AuthController.LOGIN_ASSOCIATED_USER_KEY);
        String method = (String) requestAssociationService.get(request, MFA_METHOD_KEY);
        MfaMethodHandler handler = handlerFactory.getHandler(method);
        if (handler == null) {
            throw new MalformedLoginSessionException(String.format("Session does not contain required element: %s", MFA_METHOD_KEY));
        }
        boolean submissionResult = handler.checkSubmission(MfaConfirmationSubmission.of(user, data.getCode()));
        MfaConfirmationSubmissionResultResponse result = new MfaConfirmationSubmissionResultResponse(submissionResult, user);

        return ResponseEntity.status(submissionResult ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(result);
    }
}
