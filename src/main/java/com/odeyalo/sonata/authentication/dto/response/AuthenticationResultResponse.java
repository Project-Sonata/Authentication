package com.odeyalo.sonata.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.controller.MfaController;
import com.odeyalo.sonata.authentication.dto.UserInfo;
import com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    private Set<MfaTypeMethodInfo> supportedMfaTypes;
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
        return new AuthenticationResultResponse(true, userInfo,type, MfaTypeMethodInfo.from(types), null);
    }

    public static AuthenticationResultResponse failed(ErrorDetails details) {
        return new AuthenticationResultResponse(false, null, AuthenticationResult.Type.FAILED, null, details);
    }


    @Data
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor
    public static class MfaTypeMethodInfo {
        private String name;
        // URL to start and obtain additional information about this MFA method.
        private String url;

        public static MfaTypeMethodInfo of(String name, String url) {
            return new MfaTypeMethodInfo( name, url);
        }

        public static AuthenticationResultResponse.MfaTypeMethodInfo from(UserMfaSettings.MfaType type) {
            try {
                String typeName = type.name().toLowerCase();
                String url = linkTo(methodOn(MfaController.class).startMfaProcess(null, null)).toString();
                return AuthenticationResultResponse.MfaTypeMethodInfo.of(typeName, url);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Failed to create url. Check nested exception", ex);
            }
        }

        public static Set<AuthenticationResultResponse.MfaTypeMethodInfo> from(Set<UserMfaSettings.MfaType> types) {
            return types.stream().map(MfaTypeMethodInfo::from).collect(Collectors.toCollection(HashSet::new));
        }
    }
}
