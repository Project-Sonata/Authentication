package com.odeyalo.sonata.authentication.support;

import com.odeyalo.sonata.authentication.controller.MfaController;
import com.odeyalo.sonata.authentication.dto.response.AuthenticationResultResponse;
import com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class MfaTypeMethodInfoCreator {

    public static AuthenticationResultResponse.MfaTypeMethodInfo from(UserMfaSettings.MfaType type) {
        try {
            String typeName = type.name().toLowerCase();
            String url = linkTo(methodOn(MfaController.class).descriptionLoginMfa(typeName)).toString();
            return AuthenticationResultResponse.MfaTypeMethodInfo.of(typeName,
                    url);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to create url. Check nested exception", ex);
        }
    }

    public static Set<AuthenticationResultResponse.MfaTypeMethodInfo> from(Set<UserMfaSettings.MfaType> types) {
        return types.stream().map(MfaTypeMethodInfoCreator::from).collect(Collectors.toCollection(HashSet::new));
    }
}
