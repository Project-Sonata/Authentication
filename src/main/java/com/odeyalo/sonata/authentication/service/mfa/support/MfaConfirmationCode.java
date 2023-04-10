package com.odeyalo.sonata.authentication.service.mfa.support;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.support.request.SharedRequestMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Data class stores the confirmation code that will be sent to user to confirm MFA authentication
 */
@Data
@AllArgsConstructor
@Builder
public class MfaConfirmationCode {
    private ConfirmationCode confirmationCode;
    private SharedRequestMetadata metadata;
}
