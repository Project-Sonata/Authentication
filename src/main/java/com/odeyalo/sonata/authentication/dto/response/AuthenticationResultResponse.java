package com.odeyalo.sonata.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.UserInfo;
import com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings;
import com.odeyalo.sonata.authentication.support.DescribableMfaTypeMethodCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResultResponse {
    private boolean success;
    @JsonProperty("user_info")
    private UserInfo userInfo;
    private AuthenticationResult.Type type;
    @JsonProperty("available_methods")
    private Set<DescribableMfaTypeMethod> supportedMfaTypes;
    // Used if the application cannot perform authentication
    @JsonProperty("error_details")
    private ErrorDetails errorDetails;

    public static AuthenticationResultResponse from(AuthenticationResult result) {
        if (result.isSuccess()) {
            UserInfo userInfo = UserInfo.from(result.getUser());
            return success(userInfo, result.getType(), result.getSupportedMfaTypes());
        }
        return failed(result.getErrorDetails());
    }


    public static AuthenticationResultResponse success(UserInfo userInfo,
                                                       AuthenticationResult.Type type,
                                                       Set<UserMfaSettings.MfaType> types) {
        return new AuthenticationResultResponse(true, userInfo,type, DescribableMfaTypeMethodCreator.from(types), null);
    }

    public static AuthenticationResultResponse failed(ErrorDetails details) {
        return new AuthenticationResultResponse(false, null, AuthenticationResult.Type.FAILED, null, details);
    }


    @Data
    @Builder
    public static class DescribableMfaTypeMethod {
        private String name;
        // True if this mfa type requires websocket connection
        @JsonProperty("websocket_required")
        private boolean requireWs;
        // URL to obtain additional information about this MFA method.
        private String url;
        private String description;

        public static DescribableMfaTypeMethod of(String name, boolean requireWs, String url, String description) {
            return new DescribableMfaTypeMethod(name, requireWs, url, description);
        }
    }
}
