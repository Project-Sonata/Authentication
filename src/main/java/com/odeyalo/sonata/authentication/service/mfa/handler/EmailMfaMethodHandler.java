package com.odeyalo.sonata.authentication.service.mfa.handler;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings;
import com.odeyalo.sonata.authentication.exceptions.UnsupportedUserMfaTypeException;
import com.odeyalo.sonata.authentication.service.confirmation.ConfirmationCodeManager;
import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.service.mfa.MfaConfirmationSubmission;
import com.odeyalo.sonata.authentication.service.mfa.MfaMethodInfo;
import com.odeyalo.sonata.authentication.service.mfa.support.EmailMfaConfirmationEmailMessageCreator;
import com.odeyalo.sonata.authentication.service.mfa.support.MfaConfirmationCode;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;
import com.odeyalo.sonata.authentication.service.sender.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings.MfaType.EMAIL;

/**
 * MfaMethodHandler implementation that sends the email letter with confirmation code to user's email.
 *
 * @see com.odeyalo.sonata.authentication.service.confirmation.ConfirmationCodeManager
 */
@Service
public class EmailMfaMethodHandler implements MfaMethodHandler {
    private final ConfirmationCodeManager confirmationCodeManager;
    private final MailSender mailSender;
    private final EmailMfaConfirmationEmailMessageCreator creator;

    public static final String CONTENT_MESSAGE = "We sent confirmation code to your email. Check it out!";
    public static final String MFA_TYPE = EMAIL.name().toLowerCase();

    @Autowired
    public EmailMfaMethodHandler(ConfirmationCodeManager confirmationCodeManager,
                                 EmailMfaConfirmationEmailMessageCreator creator,
                                 MailSender mailSender) {
        this.confirmationCodeManager = confirmationCodeManager;
        this.mailSender = mailSender;
        this.creator = creator;
    }

    @Override
    public MfaMethodInfo startMfaConfirmation(User user) throws UnsupportedUserMfaTypeException {
        if (!user.getUserSettings().getUserMfaSettings().isEnabledType(EMAIL)) {
            throw new UnsupportedUserMfaTypeException(String.format("The user does not have enabled: %s", EMAIL));
        }
        ConfirmationCode confirmationCode = confirmationCodeManager.generateCode(user);
        MailMessage message = creator.createMessage(new MfaConfirmationCode(confirmationCode, null), EmailReceiver.of(user.getEmail()));
        mailSender.send(message);
        return MfaMethodInfo.of(MFA_TYPE, user, CONTENT_MESSAGE);
    }

    @Override
    public boolean checkSubmission(MfaConfirmationSubmission submission) {
        String code = submission.content();
        return confirmationCodeManager.verifyCodeAndActive(code).isValid();
    }

    @Override
    public UserMfaSettings.MfaType supportedMfaType() {
        return EMAIL;
    }
}
