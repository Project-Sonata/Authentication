package com.odeyalo.sonata.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.authentication.dto.UserInfo;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GenericMfaMethodInfoResponse extends RepresentationModel<GenericMfaMethodInfoResponse> {
    @JsonProperty("require_websocket")
    private final boolean requireWs;
    private final String type = "email";
    @JsonProperty("user_info")
    private final UserInfo userInfo;
    private final String content;

    public static GenericMfaMethodInfoResponse of(UserInfo userInfo, String content, boolean requireWs) {
        return new GenericMfaMethodInfoResponse(requireWs, userInfo, content);
    }
}
