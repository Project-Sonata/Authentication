package com.odeyalo.sonata.authentication.dto.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.authentication.dto.Status;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorDetailsInfo implements Status {
    @JsonProperty("status_code")
    private int statusCode;
    @JsonProperty("status")
    private String statusName;
    @JsonProperty("error")
    private ErrorDetails errorDetails;

    public ApiErrorDetailsInfo(HttpStatus status, ErrorDetails errorDetails) {
        this.statusCode = status.value();
        this.statusName = status.getReasonPhrase();
        this.errorDetails = errorDetails;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getStatusName() {
        return statusName;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ErrorDetails {
        public static final ErrorDetails INVALID_EMAIL = of("invalid_email", "The provided email has invalid format", "To solve the problem try to enter valid email with valid pattern");
        public static final ErrorDetails EMAIL_ALREADY_TAKEN = of("email_already_taken", "The provided email already in use by other user", "To solve the problem try to enter other email");
        public static final ErrorDetails INVALID_PASSWORD = of("invalid_password", "The password is invalid, password must contain at least 8 characters and 1 number", "To fix the problem - input the correct password with required format");

        private String code;
        private String description;
        @JsonProperty("possible_solution")
        private String possibleSolution;

        public static ErrorDetails of(String code, String description, String possibleSolution) {
            return new ErrorDetails(code, description, possibleSolution);
        }
    }
}
