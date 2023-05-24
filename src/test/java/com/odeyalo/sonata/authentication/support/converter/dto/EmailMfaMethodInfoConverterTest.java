package com.odeyalo.sonata.authentication.support.converter.dto;

import com.odeyalo.sonata.authentication.controller.MfaController;
import com.odeyalo.sonata.authentication.dto.ExtendedUserInfo;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.exceptions.IllegalMfaMethodTypeException;
import com.odeyalo.sonata.authentication.service.mfa.MfaMethodInfo;
import com.odeyalo.sonata.authentication.testing.faker.UserFaker;
import com.odeyalo.sonata.common.authentication.dto.response.GenericMfaAuthenticationMethodInfoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.hateoas.Link;

import java.util.Optional;

import static com.odeyalo.sonata.authentication.support.converter.dto.EmailMfaMethodInfoConverter.MFA_EMAIL_CONFIRMATION_REL;
import static com.odeyalo.sonata.authentication.support.converter.dto.EmailMfaMethodInfoConverter.SUPPORTED_MFA_METHOD_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Tests for EmailMfaMethodInfoConverter
 */
class EmailMfaMethodInfoConverterTest {

    @Test
    @DisplayName("Convert MfaMethodInfo to GenericMfaAuthenticationMethodInfoResponse and expect valid response to be returned")
    void convert_andExpectCorrectResponseAsResult() {
        // given
        EmailMfaMethodInfoConverter converter = new EmailMfaMethodInfoConverter();

        String type = "email", content = "Miku Nakano is the best girl";
        User user = UserFaker.create().get();
        MfaMethodInfo info = new MfaMethodInfo(type, user, content);
        // when
        GenericMfaAuthenticationMethodInfoResponse actual = converter.convert(info);

        // then
        assertThat(actual)
                .as("Result must be not null!")
                .isNotNull();

        assertThat(actual.getType())
                .as("'type' must be set to: %s", SUPPORTED_MFA_METHOD_NAME)
                .isEqualTo(type);

        assertThat(actual.getUserInfo())
                .as("User info must be properly created!")
                .isEqualTo(ExtendedUserInfo.from(user));

        assertThat(actual.getContent())
                .as("Content must be the same as provided!")
                .isEqualTo(content);

        Optional<Link> link = actual.getLink(MFA_EMAIL_CONFIRMATION_REL);

        assertThat(link)
                .as("Relation with email confirmation url must be presented!")
                .isPresent()
                .hasValue(linkTo(methodOn(MfaController.class).checkMfaConfirmation(null, null)).withRel(MFA_EMAIL_CONFIRMATION_REL));
    }


    @ParameterizedTest
    @ValueSource(strings = {"mobile_app", "qrcode", "sms", "test_value"})
    @DisplayName("Convert MfaMethodInfo with wrong 'type' and expect IllegalMfaMethodTypeException to be thrown")
    void convertInvalidMfaMethodInfo_andExpectException(String invalidType) {
        EmailMfaMethodInfoConverter converter = new EmailMfaMethodInfoConverter();
        User user = UserFaker.create().get();
        String content = "melanchole";
        MfaMethodInfo invalidMfaInfo = new MfaMethodInfo(invalidType, user, content);

        assertThatThrownBy( () -> converter.convert(invalidMfaInfo))
                .as("The exception must be thrown if 'type' is not supported by this converter")
                .isExactlyInstanceOf(IllegalMfaMethodTypeException.class);
    }

    @Test
    @DisplayName("Check if correct method is supported and expect true to return")
    void supportsEmailMethod_andExpectTrueToBeReturned() {
        EmailMfaMethodInfoConverter converter = new EmailMfaMethodInfoConverter();
        String emailMethod = "email";
        assertThat(converter.supports(emailMethod))
                .as("True must be returned if 'email' method was used")
                .isTrue();
    }


    @ParameterizedTest
    @ValueSource(strings = {"mobile_app", "qrcode", "sms", "test_value"})
    @DisplayName("Check if not correct method is supported and expect false as result")
    void supportsNonCorrectMethods_andExpectFalseToBeReturned(String method) {
        EmailMfaMethodInfoConverter converter = new EmailMfaMethodInfoConverter();
        assertThat(converter.supports(method))
                .as("False must be returned if '%s' method was used", method)
                .isFalse();
    }
}
