package com.odeyalo.sonata.authentication.service.mfa;

import com.odeyalo.sonata.authentication.entity.User;
import lombok.Builder;

/**
 * Immutable class to store the info about MFA submission
 * @param user - user that need to confirm his identity
 * @param content - content that was sent by user
 */
@Builder
public record MfaConfirmationSubmission(User user, String content) {

    public static MfaConfirmationSubmission of(User user, String content) {
        return new MfaConfirmationSubmission(user, content);
    }
}
