package com.odeyalo.sonata.authentication.controller;

import com.odeyalo.sonata.authentication.controller.support.DataRequestAssociationService;
import com.odeyalo.sonata.authentication.dto.response.GenericMfaAuthenticationMethodInfoResponse;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.exceptions.MalformedLoginSessionException;
import com.odeyalo.sonata.authentication.exceptions.UnsupportedMfaMethodException;
import com.odeyalo.sonata.authentication.exceptions.UnsupportedUserMfaTypeException;
import com.odeyalo.sonata.authentication.service.mfa.MfaConfirmationSubmission;
import com.odeyalo.sonata.authentication.service.mfa.MfaMethodInfo;
import com.odeyalo.sonata.authentication.service.mfa.handler.MfaMethodHandler;
import com.odeyalo.sonata.authentication.support.converter.dto.EmailMfaMethodInfoConverter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Expose endpoints for MFA login and confirmation
 */
@RestController
@RequestMapping("/mfa")
public class MfaController {

    @Autowired
    private MfaMethodHandler handler;
    @Autowired
    private DataRequestAssociationService service;
    @Autowired
    EmailMfaMethodInfoConverter converter;


    @PostMapping("/login")
    public ResponseEntity<?> startMfaProcess(@RequestParam(name = "method") String method, HttpServletRequest request) throws MalformedLoginSessionException, UnsupportedMfaMethodException, UnsupportedUserMfaTypeException {
        if (!method.equalsIgnoreCase("email")) {
            throw new UnsupportedMfaMethodException("Method does not supported.", method);
        }

        User user = (User) service.get(request, AuthController.LOGIN_ASSOCIATED_USER_KEY);
        if (user == null) {
            throw new MalformedLoginSessionException("The session is malformed and MFA authentication cannot be performed");
        }
        // If email method is used, then we need to send confirmation code to user's email
        // and return 202 ACCEPTED
        MfaMethodInfo mfaMethodInfo = handler.startMfaConfirmation(user);

        GenericMfaAuthenticationMethodInfoResponse response = converter.convert(mfaMethodInfo);
        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/login/check")
    public ResponseEntity<?> checkMfaConfirmation(HttpServletRequest request, @RequestParam String code) {
        User user = (User) service.get(request, AuthController.LOGIN_ASSOCIATED_USER_KEY);
        boolean b = handler.checkSubmission(MfaConfirmationSubmission.of(user, code));
        Map<String, Boolean> body = Map.of("result", b);
        return ResponseEntity.ok(body);
    }
}
