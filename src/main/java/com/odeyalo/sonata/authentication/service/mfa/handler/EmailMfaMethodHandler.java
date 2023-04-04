package com.odeyalo.sonata.authentication.service.mfa.handler;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings;
import com.odeyalo.sonata.authentication.service.confirmation.ConfirmationCodeManager;
import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeEmailMessageCreator;
import com.odeyalo.sonata.authentication.service.mfa.MfaConfirmationSubmission;
import com.odeyalo.sonata.authentication.service.mfa.MfaMethodInfo;
import com.odeyalo.sonata.authentication.service.sender.MailMessage;
import com.odeyalo.sonata.authentication.service.sender.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * MfaMethodHandler implementation that sends the email letter with confirmation code to user's email.
 *
 * @see com.odeyalo.sonata.authentication.service.confirmation.ConfirmationCodeManager
 */
@Service
public class EmailMfaMethodHandler implements MfaMethodHandler {
    private final ConfirmationCodeManager confirmationCodeManager;
    private final MailSender mailSender;
    private final ConfirmationCodeEmailMessageCreator creator;

    @Autowired
    public EmailMfaMethodHandler(ConfirmationCodeManager confirmationCodeManager, MailSender mailSender, ConfirmationCodeEmailMessageCreator creator) {
        this.confirmationCodeManager = confirmationCodeManager;
        this.mailSender = mailSender;
        this.creator = creator;
    }

    @Override
    public MfaMethodInfo startMfaConfirmation(User user) {
        ConfirmationCode confirmationCode = confirmationCodeManager.generateCode(user);
        MailMessage message = creator.createMessage(confirmationCode, EmailReceiver.of(user.getEmail()));
        mailSender.send(message);
        return MfaMethodInfo.of(supportedMfaType().name().toLowerCase(), user, "We sent confirmation code to your email. Check it out!");
    }

    @Override
    public boolean checkSubmission(MfaConfirmationSubmission submission) {
        String code = submission.getContent();
        return confirmationCodeManager.verifyCodeAndActive(code).isValid();
    }

    @Override
    public UserMfaSettings.MfaType supportedMfaType() {
        return UserMfaSettings.MfaType.EMAIL;
    }
}
