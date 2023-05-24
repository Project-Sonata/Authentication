package com.odeyalo.sonata.authentication.common;

import com.odeyalo.sonata.common.shared.ErrorDetails;
import lombok.*;

/**
 * Contains info about error
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ExtendedErrorDetails extends ErrorDetails {
    public static final ExtendedErrorDetails INVALID_EMAIL = of("invalid_email", "The provided email has invalid format", "To solve the problem try to enter valid email with valid pattern");
    public static final ExtendedErrorDetails EMAIL_ALREADY_TAKEN = of("email_already_taken", "The provided email already in use by other user", "To solve the problem try to enter other email");
    public static final ExtendedErrorDetails INVALID_PASSWORD = of("invalid_password", "The password is invalid, password must contain at least 8 characters and 1 number", "To fix the problem - input the correct password with required format");
    public static final ExtendedErrorDetails SERVER_ERROR = of("SERVER_ERROR", "The error occurred on our side.", "There is no possible solution, the only thing to do is wait");



    public ExtendedErrorDetails(String code, String description, String possibleSolution) {
        super(code, description, possibleSolution);
    }

    public static ExtendedErrorDetails of(String code, String description, String possibleSolution) {
        return new ExtendedErrorDetails(code, description, possibleSolution);
    }
}
