package com.odeyalo.sonata.authentication.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represent the user credentials
 */
@NoArgsConstructor
@Data
public class LoginCredentials extends com.odeyalo.sonata.common.authentication.dto.LoginCredentials {

    public LoginCredentials(String email, String password) {
        super(email, password);
    }

    public static LoginCredentials of(String email, String password) {
        return new LoginCredentials(email, password);
    }
}
