package com.odeyalo.sonata.authentication.dto;

import org.springframework.http.HttpStatus;

/**
 * Represent the status of the response
 */
public interface Status {
    /**
     * Status code of the response.
     * @return - HTTP code of the status
     */
    int getStatusCode();

    /**
     * String representation of the status, example: for 200 OK status name will be 'SUCCESS'
     * @return - string representation of the status
     */
    String getStatusName();

    /**
     * Convert current status to the {@link HttpStatus}
     * @return - resolved HttpStatus, null otherwise
     */
    default HttpStatus toHttpStatus() {
        return HttpStatus.resolve(getStatusCode());
    }
}
