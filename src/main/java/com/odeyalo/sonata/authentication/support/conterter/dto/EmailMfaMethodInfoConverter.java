package com.odeyalo.sonata.authentication.support.conterter.dto;

import com.odeyalo.sonata.authentication.controller.MfaController;
import com.odeyalo.sonata.authentication.dto.UserInfo;
import com.odeyalo.sonata.authentication.dto.response.GenericMfaMethodInfoResponse;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.service.mfa.MfaMethodInfo;
import com.odeyalo.sonata.authentication.support.conterter.MfaMethodInfoConverter;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Convert the {@link MfaMethodInfo} to {@link GenericMfaMethodInfoResponse}
 * for email MFA type
 */
@Service
public class EmailMfaMethodInfoConverter implements MfaMethodInfoConverter<MfaMethodInfo, GenericMfaMethodInfoResponse> {
    private static final String SUPPORTED_MFA_METHOD_NAME = "email";
    private static final String MFA_EMAIL_CONFIRMATION_REL = "mfa_email_confirmation_url";

    @Override
    public GenericMfaMethodInfoResponse convert(MfaMethodInfo info) {
        User user = info.getUser();
        return GenericMfaMethodInfoResponse.of(UserInfo.from(user), info.getContent(), false)
                .add(linkTo(methodOn(MfaController.class).checkMfaConfirmation(null, null)).withRel(MFA_EMAIL_CONFIRMATION_REL));
    }

    @Override
    public boolean supports(String method) {
        return SUPPORTED_MFA_METHOD_NAME.equals(method);
    }
}
