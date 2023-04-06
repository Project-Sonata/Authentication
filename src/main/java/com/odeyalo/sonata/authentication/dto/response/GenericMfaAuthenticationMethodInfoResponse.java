package com.odeyalo.sonata.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.authentication.dto.UserInfo;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@Builder
@Data
public class GenericMfaAuthenticationMethodInfoResponse extends RepresentationModel<GenericMfaAuthenticationMethodInfoResponse> {
    @JsonProperty("require_websocket")
    private final boolean requireWs;
    private final String type;
    @JsonProperty("user_info")
    private final UserInfo userInfo;
    private final String content;

    public static GenericMfaAuthenticationMethodInfoResponse of(UserInfo userInfo, String type, String content, boolean requireWs) {
        return new GenericMfaAuthenticationMethodInfoResponse(requireWs, type, userInfo, content);
    }
}
