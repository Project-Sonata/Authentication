package com.odeyalo.sonata.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.authentication.dto.UserInfo;
import com.odeyalo.sonata.authentication.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result response of the mfa confirmation submission
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Data
public class MfaConfirmationSubmissionResultResponse {
    private boolean result;
    @JsonProperty("user_info")
    private UserInfo info;

    public MfaConfirmationSubmissionResultResponse(boolean result, User user) {
        this.result = result;
        if (result) {
            this.info = UserInfo.from(user);
        }
    }

    public static MfaConfirmationSubmissionResultResponse success(User user) {
        return new MfaConfirmationSubmissionResultResponse(true, UserInfo.from(user));
    }

    public static MfaConfirmationSubmissionResultResponse failed() {
        return new MfaConfirmationSubmissionResultResponse(false, (UserInfo) null);
    }
}
