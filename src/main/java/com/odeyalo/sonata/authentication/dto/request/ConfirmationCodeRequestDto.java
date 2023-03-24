package com.odeyalo.sonata.authentication.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contains the confirmation code that used to activate the account
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmationCodeRequestDto {
    @JsonProperty("code")
    private String codeValue;
}
