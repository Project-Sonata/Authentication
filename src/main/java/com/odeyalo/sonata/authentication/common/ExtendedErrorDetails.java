package com.odeyalo.sonata.authentication.common;

import com.odeyalo.sonata.common.authentication.AuthenticationErrorCodes;
import com.odeyalo.sonata.common.shared.ErrorDetails;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Contains info about error
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ExtendedErrorDetails extends ErrorDetails {
    public static final ExtendedErrorDetails INVALID_EMAIL = of(AuthenticationErrorCodes.INVALID_EMAIL, "The provided email has invalid format", "To solve the problem try to enter valid email with valid pattern");
    public static final ExtendedErrorDetails EMAIL_ALREADY_TAKEN = of(AuthenticationErrorCodes.EMAIL_ALREADY_TAKEN, "The provided email already in use by other user", "To solve the problem try to enter other email");
    public static final ExtendedErrorDetails INVALID_PASSWORD = of(AuthenticationErrorCodes.INVALID_PASSWORD, "The password is invalid, password must contain at least 8 characters and 1 number", "To fix the problem - input the correct password with required format");
    public static final ExtendedErrorDetails SERVER_ERROR = of(AuthenticationErrorCodes.SERVER_ERROR, "The error occurred on our side.", "There is no possible solution, the only thing to do is wait");



    public ExtendedErrorDetails(String code, String description, String possibleSolution) {
        super(code, description, possibleSolution);
    }

    public static ExtendedErrorDetails of(String code, String description, String possibleSolution) {
        return new ExtendedErrorDetails(code, description, possibleSolution);
    }
}
