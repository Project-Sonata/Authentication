package com.odeyalo.sonata.authentication.service.password;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.common.shared.ErrorDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RepositorySecureUserPasswordUpdater implements SecureUserPasswordUpdater {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public RepositorySecureUserPasswordUpdater(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public PasswordUpdatingResult updatePassword(long userId, PasswordContainer container) {
        Optional<User> optional = userRepository.findById(userId);
        if (optional.isEmpty()) {
            return PasswordUpdatingResult.failed(ErrorDetails.of("user_not_found", null, null));
        }
        User user = optional.get();
        String realOldPassword = user.getPassword();
        if (!encoder.matches(container.getOldPassword(), realOldPassword)) {
            return PasswordUpdatingResult.failed(ErrorDetails.of("old_password_mismatch", null, null));
        }
        String newPassword = encoder.encode(container.getNewPassword());
        user.setPassword(newPassword);
        userRepository.save(user);
        return PasswordUpdatingResult.updated();
    }
}
