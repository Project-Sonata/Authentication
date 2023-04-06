package com.odeyalo.sonata.authentication.exceptions.handler;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.error.ApiErrorDetailsInfo;
import com.odeyalo.sonata.authentication.exceptions.MalformedLoginSessionException;
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

    @ExceptionHandler(MalformedLoginSessionException.class)
    public ResponseEntity<?> handleMalformedLoginSessionException(MalformedLoginSessionException ex) {
        ErrorDetails invalidSession = ErrorDetails.of(
                "invalid_session",
                "Login cannot be performed since login session is malformed",
                "Try to login using credentials.");
        ApiErrorDetailsInfo info = new ApiErrorDetailsInfo(HttpStatus.BAD_REQUEST, invalidSession);
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(info);
    }
}
