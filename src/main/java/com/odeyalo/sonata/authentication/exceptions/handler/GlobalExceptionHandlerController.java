package com.odeyalo.sonata.authentication.exceptions.handler;

import com.odeyalo.sonata.authentication.common.ExtendedErrorDetails;
import com.odeyalo.sonata.authentication.dto.ErrorAdditionalInfoKeys;
import com.odeyalo.sonata.authentication.exceptions.MalformedLoginSessionException;
import com.odeyalo.sonata.authentication.exceptions.MissingConfirmationCodeValueException;
import com.odeyalo.sonata.authentication.exceptions.UnsupportedMfaMethodException;
import com.odeyalo.sonata.common.shared.ApiErrorDetailsInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.odeyalo.sonata.authentication.common.ExtendedErrorDetails.of;

/**
 * A simple controller that used to handle all occurred NOT handled exceptions and return it to used as response
 */
@RestControllerAdvice
public class GlobalExceptionHandlerController {
    public static final ExtendedErrorDetails MFA_METHOD_DOES_NOT_SUPPORTED = of("invalid_mfa_method",
            "This method does not supported", "Use valid MFA method");

    public static final ExtendedErrorDetails INVALID_SESSION = of("invalid_session",
            "Login cannot be performed since login session is malformed", "Try to login using credentials.");

    public static final ExtendedErrorDetails MISSING_CONFIRMATION_CODE_VALUE = of("missing_confirmation_code",
            "The request does not contain confirmation code", "You should add confirmation code to request body");

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

    @ExceptionHandler(MissingConfirmationCodeValueException.class)
    private ResponseEntity<?> handleMissingConfirmationCodeValueException(MissingConfirmationCodeValueException ex) {
        ApiErrorDetailsInfo body = new ApiErrorDetailsInfo(HttpStatus.BAD_REQUEST, MISSING_CONFIRMATION_CODE_VALUE);
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(body);
    }
}
