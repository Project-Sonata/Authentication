package com.odeyalo.sonata.authentication.support.event.handler;

import com.odeyalo.sonata.authentication.support.user.RemoteUserCreationService;
import com.odeyalo.sonata.common.user.dto.CreateUserPayloadDto;
import com.odeyalo.sonata.suite.brokers.events.user.UserRegisteredEvent;
import com.odeyalo.sonata.suite.brokers.events.user.data.UserRegisteredEventData;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;
import java.time.Month;

import static com.odeyalo.sonata.authentication.support.user.RemoteUserCreationService.CreationResult.RegistryStatus.SUCCESS;
import static com.odeyalo.sonata.common.user.dto.CreateUserPayloadDto.Gender.FEMALE;
import static org.assertj.core.api.Assertions.assertThat;

class RemoteServiceUserRegistryUserRegisteredEventHandlerTest {

    @Test
    void shouldSendRegisteredUserToRemoteService() {
        final var spyRegistrar = new SpyRegistrar();

        final var testable = new RemoteServiceUserRegistryUserRegisteredEventHandler(
                spyRegistrar
        );

        final UserRegisteredEventData eventData = UserRegisteredEventData.builder()
                .id("miku")
                .birthdate(LocalDate.of(2004, Month.MAY, 22))
                .gender("FEMALE")
                .enableNotification(true)
                .email("mikuNakano@gmail.com")
                .build();

        testable.onEvent(new UserRegisteredEvent(eventData));

        assertThat(spyRegistrar.isWasSent()).isTrue();
    }

    @Test
    void shouldSendRegisteredUserToRemoteServiceWithCorrectPayload() {
        final var spyRegistrar = new SpyRegistrar();

        final var testable = new RemoteServiceUserRegistryUserRegisteredEventHandler(
                spyRegistrar
        );

        final UserRegisteredEventData eventData = UserRegisteredEventData.builder()
                .id("miku")
                .birthdate(LocalDate.of(2004, Month.MAY, 22))
                .gender("FEMALE")
                .enableNotification(true)
                .email("mikuNakano@gmail.com")
                .build();

        testable.onEvent(new UserRegisteredEvent(eventData));

        assertThat(spyRegistrar.getPayloadThatWasSent().getId()).isEqualTo("miku");
        assertThat(spyRegistrar.getPayloadThatWasSent().getBirthdate()).isEqualTo("2004-05-22");
        assertThat(spyRegistrar.getPayloadThatWasSent().getUsername()).isEqualTo("miku");
        assertThat(spyRegistrar.getPayloadThatWasSent().getEmail()).isEqualTo("mikuNakano@gmail.com");
        assertThat(spyRegistrar.getPayloadThatWasSent().getGender()).isEqualTo(FEMALE);
    }

    static class SpyRegistrar implements RemoteUserCreationService {
        private  CreateUserPayloadDto payloadThatWasSent;
        private boolean wasSent;

        @Override
        public CreationResult createUser(final CreateUserPayloadDto userPayload) {
            wasSent = true;
            payloadThatWasSent = userPayload;
            return new CreationResult(SUCCESS, URI.create("localhost:3000/user/123"), null);
        }

        public CreateUserPayloadDto getPayloadThatWasSent() {
            return payloadThatWasSent;
        }

        public boolean isWasSent() {
            return wasSent;
        }
    }
}