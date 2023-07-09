package com.odeyalo.sonata.authentication.dto.request;

import com.odeyalo.sonata.common.authentication.dto.request.UserRegistrationInfo;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Data class contains the info that was provided by user in registration form.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@SuperBuilder
public class AdvancedUserRegistrationInfo extends UserRegistrationInfo {
    private String countryCode;
}


