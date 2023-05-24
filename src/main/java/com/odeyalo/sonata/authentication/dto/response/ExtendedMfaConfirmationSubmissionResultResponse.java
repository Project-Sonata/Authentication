package com.odeyalo.sonata.authentication.dto.response;

import com.odeyalo.sonata.authentication.dto.ExtendedUserInfo;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.common.authentication.dto.UserInfo;
import com.odeyalo.sonata.common.authentication.dto.response.MfaConfirmationSubmissionResultResponse;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Result response of the mfa confirmation submission
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class ExtendedMfaConfirmationSubmissionResultResponse extends MfaConfirmationSubmissionResultResponse {

    public ExtendedMfaConfirmationSubmissionResultResponse(boolean result, UserInfo userInfo) {
        this.result = result;
        if (result) {
            this.info = userInfo;
        }
    }

    public static ExtendedMfaConfirmationSubmissionResultResponse success(User user) {
        return new ExtendedMfaConfirmationSubmissionResultResponse(true, ExtendedUserInfo.from(user));
    }

    public static ExtendedMfaConfirmationSubmissionResultResponse failed() {
        return new ExtendedMfaConfirmationSubmissionResultResponse(false,null);
    }

    public static ExtendedMfaConfirmationSubmissionResultResponse of(boolean submissionResult, User user) {
        return new ExtendedMfaConfirmationSubmissionResultResponse(submissionResult, ExtendedUserInfo.from(user));
    }
}
