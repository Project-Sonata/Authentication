package com.odeyalo.sonata.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResultResponse {
    private boolean success;
    @JsonProperty("user_info")
    private UserInfo userInfo;
    @JsonProperty("error_details")
    private ErrorDetails errorDetails;

    public AuthenticationResultResponse(boolean success, UserInfo userInfo) {
        this.success = success;
        this.userInfo = userInfo;
    }

    public static AuthenticationResultResponse from(AuthenticationResult result) {
        if (result.isSuccess()) {
            UserInfo userInfo = UserInfo.from(result.getUser());
            return success(userInfo);
        }
        return failed(result.getErrorDetails());
    }


    public static AuthenticationResultResponse success(UserInfo userInfo) {
        return new AuthenticationResultResponse(true, userInfo);
    }

    public static AuthenticationResultResponse failed(ErrorDetails details) {
        return new AuthenticationResultResponse(false, null, details);
    }
}
