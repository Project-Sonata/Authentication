package com.odeyalo.sonata.authentication.service.mfa.support;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;
import com.odeyalo.sonata.authentication.support.request.SharedRequestMetadata;
import com.odeyalo.sonata.authentication.support.web.http.HttpSharedRequestMetadata;
import com.odeyalo.sonata.authentication.testing.assertations.MailMessageAssert;
import com.odeyalo.sonata.authentication.testing.faker.ConfirmationCodeFaker;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static com.odeyalo.sonata.authentication.support.email.message.EmailMessageTypeCode.MFA_EMAIL_CONFIRMATION_MESSAGE;

class PlainTextEmailMfaConfirmationEmailMessageCreatorTest {

    @Test
    void createMessage() {
        // given
        PlainTextEmailMfaConfirmationEmailMessageCreator creator = new PlainTextEmailMfaConfirmationEmailMessageCreator();
        ConfirmationCode code = ConfirmationCodeFaker.numeric().activated().get();
        SharedRequestMetadata sharedRequestMetadata = new HttpSharedRequestMetadata(new MockHttpServletRequest());
        EmailReceiver receiver = EmailReceiver.of("odeyalo@gmail.com");
        // when
        MailMessage message = creator.createMessage(new MfaConfirmationCode(code, sharedRequestMetadata), receiver);
        // then
        MailMessageAssert.forMessage(message)
                .type(MFA_EMAIL_CONFIRMATION_MESSAGE)
                .receiver()
                .exactlyEquals(receiver)
                    .and()
                .content()
                .exactlyEquals("You're trying to login using MFA. Here is your code: " + code.getCode())
                .and();
    }
}
