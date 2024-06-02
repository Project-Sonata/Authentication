package com.odeyalo.sonata.authentication.support.event.handler;

import com.odeyalo.sonata.authentication.support.user.RemoteUserCreationService;
import com.odeyalo.sonata.common.user.dto.CreateUserPayloadDto;
import com.odeyalo.sonata.common.user.dto.CreateUserPayloadDto.Gender;
import com.odeyalo.sonata.suite.brokers.events.user.UserRegisteredEvent;
import com.odeyalo.sonata.suite.brokers.events.user.data.UserRegisteredEventData;
import org.apache.commons.lang3.EnumUtils;

public final class RemoteServiceUserRegistryUserRegisteredEventHandler implements UserRegisteredEventHandler {
    private final RemoteUserCreationService userRegistrar;

    public RemoteServiceUserRegistryUserRegisteredEventHandler(final RemoteUserCreationService userRegistrar) {
        this.userRegistrar = userRegistrar;
    }

    @Override
    public void onEvent(final UserRegisteredEvent event) {
        final UserRegisteredEventData eventPayload = event.getBody();

        final CreateUserPayloadDto payloadDto = CreateUserPayloadDto.builder()
                .birthdate(eventPayload.getBirthdate())
                .id(eventPayload.getId())
                .countryCode(eventPayload.getCountryCode())
                .email(eventPayload.getEmail())
                .username(eventPayload.getId())
                .gender(EnumUtils.getEnumIgnoreCase(Gender.class, eventPayload.getGender()))
                .build();

        userRegistrar.createUser(payloadDto);
    }
}
