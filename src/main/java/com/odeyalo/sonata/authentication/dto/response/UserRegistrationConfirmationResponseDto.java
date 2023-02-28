package com.odeyalo.sonata.authentication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

/**
 * <p>
 * Simple response dto that used to return confirmation url and message to user after submitting valid registration form
 * The DTO supports HATEOAS for link representation in JSON
 * /p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserRegistrationConfirmationResponseDto extends RepresentationModel<UserRegistrationConfirmationResponseDto> {
    private String message;
}
