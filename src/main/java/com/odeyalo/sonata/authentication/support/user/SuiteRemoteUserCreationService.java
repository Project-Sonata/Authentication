package com.odeyalo.sonata.authentication.support.user;

import com.odeyalo.sonata.common.user.dto.CreateUserPayloadDto;
import com.odeyalo.sonata.suite.servlet.client.UserServiceClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.net.URI;

import static com.odeyalo.sonata.authentication.support.user.RemoteUserCreationService.CreationResult.RegistryStatus.SUCCESS;

@Component
public final class SuiteRemoteUserCreationService implements RemoteUserCreationService {
    private final UserServiceClient userClient;

    public SuiteRemoteUserCreationService(final UserServiceClient userClient) {
        this.userClient = userClient;
    }

    @Override
    public CreationResult createUser(final CreateUserPayloadDto userPayload) {
        ResponseEntity<Void> response = userClient.createUser(userPayload);

        final URI resourceLocation = response.getHeaders().getLocation();

        return new CreationResult(SUCCESS, resourceLocation, null);
    }
}
