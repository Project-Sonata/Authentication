package com.odeyalo.sonata.authentication.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data class contains the info that was provided by user in registration form.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegistrationInfo {
    private String email;
    private String password;
    private String gender;
    private LocalDate birthdate;
    @JsonProperty("notification_enabled")
    private boolean notificationEnabled;
}


