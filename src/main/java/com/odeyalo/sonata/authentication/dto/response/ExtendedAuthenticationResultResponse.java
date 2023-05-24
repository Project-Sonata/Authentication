package com.odeyalo.sonata.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.controller.MfaController;
import com.odeyalo.sonata.authentication.dto.ExtendedUserInfo;
import com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings;
import com.odeyalo.sonata.common.authentication.dto.AuthenticationProcessType;
import com.odeyalo.sonata.common.authentication.dto.response.AuthenticationResultResponse;
import com.odeyalo.sonata.common.shared.ErrorDetails;
import lombok.*;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExtendedAuthenticationResultResponse extends AuthenticationResultResponse {

    public ExtendedAuthenticationResultResponse(boolean success, com.odeyalo.sonata.common.authentication.dto.UserInfo userInfo, AuthenticationProcessType type, Set<AuthenticationResultResponse.MfaTypeMethodInfo> supportedMfaTypes,
                                                com.odeyalo.sonata.common.shared.ErrorDetails errorDetails) {
        super(success, userInfo, type, supportedMfaTypes, errorDetails);
    }

    public static ExtendedAuthenticationResultResponse from(AuthenticationResult result) {
        if (result.isSuccess()) {
            ExtendedUserInfo userInfo = ExtendedUserInfo.from(result.getUser());
            return success(userInfo, result.getType(), result.getSupportedMfaTypes());
        }
        return failed(result.getErrorDetails());
    }


    public static ExtendedAuthenticationResultResponse success(ExtendedUserInfo userInfo,
                                                               AuthenticationResult.Type type,
                                                               Set<UserMfaSettings.MfaType> types) {

        Set<AuthenticationResultResponse.MfaTypeMethodInfo> infos = MfaTypeMethodInfo.from(types).stream()
                .map(methodInfo -> AuthenticationResultResponse.MfaTypeMethodInfo.of(methodInfo.name, methodInfo.url))
                .collect(Collectors.toSet());

        return new ExtendedAuthenticationResultResponse(true, userInfo, AuthenticationProcessType.valueOf(type.getName().toUpperCase(Locale.ROOT)),
                infos, null);
    }

    public static ExtendedAuthenticationResultResponse failed(ErrorDetails details) {
        return new ExtendedAuthenticationResultResponse(false, null, AuthenticationProcessType.FAILED, null, details);
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

        public static ExtendedAuthenticationResultResponse.MfaTypeMethodInfo from(UserMfaSettings.MfaType type) {
            try {
                String typeName = type.name().toLowerCase();
                String url = linkTo(methodOn(MfaController.class).startMfaProcess(type.name().toLowerCase(), null, null)).toString();
                return ExtendedAuthenticationResultResponse.MfaTypeMethodInfo.of(typeName, url);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Failed to create url. Check nested exception", ex);
            }
        }

        public static Set<ExtendedAuthenticationResultResponse.MfaTypeMethodInfo> from(Set<UserMfaSettings.MfaType> types) {
            return types.stream().map(MfaTypeMethodInfo::from).collect(Collectors.toCollection(HashSet::new));
        }
    }
}
