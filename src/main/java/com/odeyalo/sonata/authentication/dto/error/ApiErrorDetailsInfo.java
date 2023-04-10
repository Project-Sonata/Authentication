package com.odeyalo.sonata.authentication.dto.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

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
    @JsonProperty("info")
    private Map<String, Object> additionalInfo = new HashMap<>();

    public ApiErrorDetailsInfo(int statusCode, String statusName, ErrorDetails errorDetails) {
        this.statusCode = statusCode;
        this.statusName = statusName;
        this.errorDetails = errorDetails;
    }

    public ApiErrorDetailsInfo(HttpStatus status, ErrorDetails errorDetails) {
        this.statusCode = status.value();
        this.statusName = status.getReasonPhrase();
        this.errorDetails = errorDetails;
    }

    public ApiErrorDetailsInfo addInfo(String key, Object value) {
        additionalInfo.put(key, value);
        return this;
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
