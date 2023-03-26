package com.odeyalo.sonata.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResultResponse {
    private boolean success;
    @JsonProperty("user_info")
    private UserInfo userInfo;

    public static AuthenticationResultResponse from(AuthenticationResult result) {
        if (result.isSuccess()) {
            UserInfo userInfo = UserInfo.from(result.getUser());
            return success(userInfo);
        }
        return failed();
    }


    public static AuthenticationResultResponse success(UserInfo userInfo) {
        return new AuthenticationResultResponse(true, userInfo);
    }

    public static AuthenticationResultResponse failed() {
        return new AuthenticationResultResponse(false, null);
    }
}
