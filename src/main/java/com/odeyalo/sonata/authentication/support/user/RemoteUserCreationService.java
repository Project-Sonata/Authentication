package com.odeyalo.sonata.authentication.support.user;

import com.odeyalo.sonata.common.shared.ErrorDetails;
import com.odeyalo.sonata.common.user.dto.CreateUserPayloadDto;

import java.net.URI;

/**
 * Create the user in remote service using any method(http, grpc, etc)
 */
public interface RemoteUserCreationService {

    CreationResult createUser(CreateUserPayloadDto userPayload);

    /**
     * Wrapper for result of the user registration
     * @param status - status of registration, never null
     * @param resourceUri - URI at which User resource can be accessed, null only if {@link RegistryStatus#ERROR}
     * @param errorDetails - details about the error, null only if {@link RegistryStatus#SUCCESS}
     */
    record CreationResult(RegistryStatus status,
                          URI resourceUri,
                          ErrorDetails errorDetails) {

        public enum RegistryStatus {
            SUCCESS,
            ERROR
        }
    }
}
