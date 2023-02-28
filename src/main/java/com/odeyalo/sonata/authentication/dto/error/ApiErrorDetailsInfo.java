package com.odeyalo.sonata.authentication.dto.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorDetailsInfo implements Status {
    @JsonProperty("status_code")
    private int statusCode;
    @JsonProperty("status")
    private String statusName;
    @JsonProperty("error")
    private ErrorDetails errorDetails;

    public ApiErrorDetailsInfo(HttpStatus status, ErrorDetails errorDetails) {
        this.statusCode = status.value();
        this.statusName = status.getReasonPhrase();
        this.errorDetails = errorDetails;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getStatusName() {
        return statusName;
    }
}
