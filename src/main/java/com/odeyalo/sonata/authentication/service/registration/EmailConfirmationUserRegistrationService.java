package com.odeyalo.sonata.authentication.service.registration;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.authentication.service.confirmation.EmailConfirmationCodeGeneratorSender;
import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmailConfirmationUserRegistrationService implements UserRegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailConfirmationCodeGeneratorSender confirmationCodeGeneratorSender;
    private final Logger logger = LoggerFactory.getLogger(EmailConfirmationUserRegistrationService.class);

    @Autowired
    public EmailConfirmationUserRegistrationService(UserRepository userRepository,
                                                    EmailConfirmationCodeGeneratorSender confirmationCodeGeneratorSender,
                                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.confirmationCodeGeneratorSender = confirmationCodeGeneratorSender;
    }

    @Override
    public RegistrationResult registerUser(UserRegistrationInfo info) {
        User user = createUser(info);
        RegistrationResult result = save(user);
        if (result.success()) {
            sendConfirmationCode(info);
        }
        return result;
    }

    private void sendConfirmationCode(UserRegistrationInfo info) {
        confirmationCodeGeneratorSender.generateAndSend(EmailReceiver.of(info.getEmail()));
    }

    /**
     * Save the user in repository
     * @param user - user to save
     * @return - {@link RegistrationResult#success()})} if everything is okay, {@link RegistrationResult#failed(RegistrationResult.RequiredAction, ErrorDetails)}) otherwise
     */
    private RegistrationResult save(User user) {
        try {
            this.userRepository.save(user);
            return RegistrationResult.success(RegistrationResult.RequiredAction.CONFIRM_EMAIL);
        } catch (Exception e) {
            this.logger.error("The error occurred during user saving.", e);
            // We already validate the UserRegistrationInfo in the UserRegistrationManager and if error occurs we don't know what type of the error, so we return SERVER_ERROR
            return RegistrationResult.failed(RegistrationResult.RequiredAction.DO_NOTHING, ErrorDetails.SERVER_ERROR);
        }
    }

    private User createUser(UserRegistrationInfo info) {
        String password = passwordEncoder.encode(info.getPassword());
        return User
                .builder()
                .email(info.getEmail())
                .password(password)
                .active(false)
                .build();
    }
}
