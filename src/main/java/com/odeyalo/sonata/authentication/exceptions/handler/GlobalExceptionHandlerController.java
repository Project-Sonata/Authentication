package com.odeyalo.sonata.authentication.exceptions.handler;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.ErrorAdditionalInfoKeys;
import com.odeyalo.sonata.authentication.dto.error.ApiErrorDetailsInfo;
import com.odeyalo.sonata.authentication.exceptions.MalformedLoginSessionException;
import com.odeyalo.sonata.authentication.exceptions.UnsupportedMfaMethodException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * A simple controller that used to handle all occurred NOT handled exceptions and return it to used as response
 */
@RestControllerAdvice
public class GlobalExceptionHandlerController {
    public static final ErrorDetails MFA_METHOD_DOES_NOT_SUPPORTED = ErrorDetails.of("invalid_mfa_method",
            "This method does not supported", "Use valid MFA method");

    public static final ErrorDetails INVALID_SESSION = ErrorDetails.of("invalid_session",
            "Login cannot be performed since login session is malformed", "Try to login using credentials.");

    @ExceptionHandler(MalformedLoginSessionException.class)
    public ResponseEntity<?> handleMalformedLoginSessionException(MalformedLoginSessionException ex) {
        ApiErrorDetailsInfo info = new ApiErrorDetailsInfo(HttpStatus.BAD_REQUEST, INVALID_SESSION);
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(info);
    }

    @ExceptionHandler(UnsupportedMfaMethodException.class)
    public ResponseEntity<?> handleUnsupportedMfaMethodException(UnsupportedMfaMethodException ex) {
        ApiErrorDetailsInfo details = new ApiErrorDetailsInfo(HttpStatus.BAD_REQUEST, MFA_METHOD_DOES_NOT_SUPPORTED)
                        .addInfo(ErrorAdditionalInfoKeys.UNSUPPORTED_MFA_METHOD, ex.getMethod());

        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(details);
    }
}
