package com.odeyalo.sonata.authentication.service.registration;

import com.odeyalo.sonata.authentication.common.ExtendedErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.exceptions.MessageSendingFailedException;
import com.odeyalo.sonata.authentication.repository.AdvancedUserRegistrationInfoStore;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.authentication.service.confirmation.EmailConfirmationCodeGeneratorSender;
import com.odeyalo.sonata.authentication.service.confirmation.EmailReceiver;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

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
    public RegistrationResult registerUser(AdvancedUserRegistrationInfo info) {
        User user = createUser(info);
        ResultWrapper wrapper = saveUser(user);

        if (wrapper.isFailed()) {
            this.logger.warn("Failed to register the user: {}", wrapper);
            return wrapper.result();
        }

        return tryToSendOr(user, wrapper, (resultWrapper) -> {
            userRepository.deleteById(resultWrapper.user().getId());
        });
    }

    private RegistrationResult tryToSendOr(User user, ResultWrapper wrapper, Consumer<ResultWrapper> onError) {
        try {
            sendConfirmationCode(user);
            return wrapper.result();
        } catch (MessageSendingFailedException ex) {
            this.logger.error("Failed to send the confirmation code to: {}", user.getEmail());
            onError.accept(wrapper);
            return RegistrationResult.failed(RegistrationResult.RequiredAction.DO_NOTHING, ExtendedErrorDetails.SERVER_ERROR);
        }
    }

    private void sendConfirmationCode(User user) throws MessageSendingFailedException {
        confirmationCodeGeneratorSender.generateAndSend(user, EmailReceiver.of(user.getEmail()));
    }

    /**
     * Save the user in repository
     * @param user - user to save
     * @return - {@link RegistrationResult#success()})} if everything is okay, {@link RegistrationResult#failed(RegistrationResult.RequiredAction, ExtendedErrorDetails)}) otherwise
     */
    private ResultWrapper saveUser(User user) {
        try {
            User savedUser = this.userRepository.save(user);
            RegistrationResult result = RegistrationResult.success(RegistrationResult.RequiredAction.CONFIRM_EMAIL);
            return ResultWrapper.of(savedUser, result);
        } catch (Exception e) {
            this.logger.error("The error occurred during user saving.", e);
            // We already validate the UserRegistrationInfo in the UserRegistrationManager and if error occurs we don't know what type of the error, so we return SERVER_ERROR
            RegistrationResult result = RegistrationResult.failed(RegistrationResult.RequiredAction.DO_NOTHING, ExtendedErrorDetails.SERVER_ERROR);
            return ResultWrapper.of(null, result);
        }
    }

    private User createUser(AdvancedUserRegistrationInfo info) {
        String password = passwordEncoder.encode(info.getPassword());
        return User
                .builder()
                .email(info.getEmail())
                .password(password)
                .active(false)
                .build();
    }

    /**
     * Simple wrapper to wrap user and registration result in the one class
     * If result is failed, then user must be null
     * @param user   - user that was saved
     * @param result - result of the registration
     */
    private record ResultWrapper(User user, RegistrationResult result) {

        public boolean isFailed() {
            return BooleanUtils.isFalse(result.success());
        }

        public boolean isSuccess() {
            return result.success();
        }

        public static ResultWrapper of(User user, RegistrationResult result) {
            return new ResultWrapper(user, result);
        }
    }
}
