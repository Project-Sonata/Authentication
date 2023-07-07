package com.odeyalo.sonata.authentication.dto.response;

import com.odeyalo.sonata.authentication.dto.ExtendedUserInfo;
import com.odeyalo.sonata.common.authentication.dto.response.GenericMfaAuthenticationMethodInfoResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExtendedGenericMfaAuthenticationMethodInfoResponse extends GenericMfaAuthenticationMethodInfoResponse {

    public ExtendedGenericMfaAuthenticationMethodInfoResponse(boolean requireWs, String type, ExtendedUserInfo userInfo, String content) {
        super(requireWs, type, userInfo, content);
    }

    public static ExtendedGenericMfaAuthenticationMethodInfoResponse of(ExtendedUserInfo userInfo, String type, String content, boolean requireWs) {
        return new ExtendedGenericMfaAuthenticationMethodInfoResponse(requireWs, type, userInfo, content);
    }

    // A -> B -> C -> GenericMfaAuthenticationMethodInfoResponse
}
