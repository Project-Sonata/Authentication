package com.odeyalo.sonata.authentication.support.converter.dto;

import com.odeyalo.sonata.authentication.controller.MfaController;
import com.odeyalo.sonata.authentication.dto.UserInfo;
import com.odeyalo.sonata.authentication.dto.response.GenericMfaAuthenticationMethodInfoResponse;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.service.mfa.MfaMethodInfo;
import com.odeyalo.sonata.authentication.support.converter.MfaMethodInfoConverter;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Convert the {@link MfaMethodInfo} to {@link GenericMfaAuthenticationMethodInfoResponse}
 * for email MFA type
 */
@Service
public class EmailMfaMethodInfoConverter implements MfaMethodInfoConverter<MfaMethodInfo, GenericMfaAuthenticationMethodInfoResponse> {
    private static final String SUPPORTED_MFA_METHOD_NAME = "email";
    private static final String MFA_EMAIL_CONFIRMATION_REL = "mfa_email_confirmation_url";

    @Override
    public GenericMfaAuthenticationMethodInfoResponse convert(MfaMethodInfo info) {
        User user = info.getUser();
        return GenericMfaAuthenticationMethodInfoResponse.of(UserInfo.from(user), SUPPORTED_MFA_METHOD_NAME, info.getContent(), false)
                .add(linkTo(methodOn(MfaController.class).checkMfaConfirmation(null, null)).withRel(MFA_EMAIL_CONFIRMATION_REL));
    }

    @Override
    public boolean supports(String method) {
        return SUPPORTED_MFA_METHOD_NAME.equals(method);
    }
}
