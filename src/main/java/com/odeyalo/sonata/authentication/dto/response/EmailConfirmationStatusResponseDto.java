package com.odeyalo.sonata.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.authentication.dto.ExtendedUserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Dto to return an answer about email confirmation status
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE)
public class EmailConfirmationStatusResponseDto {
    @JsonProperty("is_confirmed")
    private final boolean isConfirmed;
    @JsonProperty("user_info")
    private final ExtendedUserInfo userInfo;
    @JsonProperty("message")
    private final String message;

    public EmailConfirmationStatusResponseDto() {
        this.isConfirmed = false;
        this.userInfo = null;
        this.message = null;
    }

    public static final String SUCCESS_MESSAGE = "The email has been successfully confirmed";

    public static EmailConfirmationStatusResponseDto confirmationSuccess(ExtendedUserInfo userInfo) {
        return new EmailConfirmationStatusResponseDto(true, userInfo, SUCCESS_MESSAGE);
    }

    public static EmailConfirmationStatusResponseDto confirmationFailed(String message) {
        return new EmailConfirmationStatusResponseDto(false, null, message);
    }
}
