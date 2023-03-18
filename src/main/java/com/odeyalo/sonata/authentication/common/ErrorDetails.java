package com.odeyalo.sonata.authentication.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contains info about error
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorDetails {
    public static final ErrorDetails INVALID_EMAIL = of("invalid_email", "The provided email has invalid format", "To solve the problem try to enter valid email with valid pattern");
    public static final ErrorDetails EMAIL_ALREADY_TAKEN = of("email_already_taken", "The provided email already in use by other user", "To solve the problem try to enter other email");
    public static final ErrorDetails INVALID_PASSWORD = of("invalid_password", "The password is invalid, password must contain at least 8 characters and 1 number", "To fix the problem - input the correct password with required format");
    public static final ErrorDetails SERVER_ERROR = of("SERVER_ERROR", "The error occurred on our side.", "There is no possible solution, the only thing to do is wait");

    private String code;
    private String description;
    @JsonProperty("possible_solution")
    private String possibleSolution;

    public static ErrorDetails of(String code, String description, String possibleSolution) {
        return new ErrorDetails(code, description, possibleSolution);
    }
}
