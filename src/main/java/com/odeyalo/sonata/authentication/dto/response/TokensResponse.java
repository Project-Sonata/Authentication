package com.odeyalo.sonata.authentication.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.authentication.dto.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokensResponse implements Status {
    private int status;
    @JsonProperty("status_name")
    private String statusName;
    private Tokens tokens;

    public TokensResponse(HttpStatus status, Tokens tokens) {
        this.status = status.value();
        this.statusName = status.name();
        this.tokens = tokens;
    }

    @Override
    @JsonIgnore
    public int getStatusCode() {
        return status;
    }

    @Override
    public String getStatusName() {
        return statusName;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Tokens {
        @JsonProperty("access_token")
        private Token accessToken;
        @JsonProperty("refresh_token")
        private Token refreshToken;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Token {
        @JsonProperty("body")
        private String body;
        @JsonProperty("expires_in")
        private Integer expiresIn;
    }
}
