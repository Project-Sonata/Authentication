package com.odeyalo.sonata.authentication.service.registration;

import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class EmailConfirmationUserRegistrationService implements UserRegistrationService {
    private final UserRepository userRepository;

    public EmailConfirmationUserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public RegistrationResult registerUser(UserRegistrationInfo info) {
        User user = User
                .builder()
                .email(info.getEmail())
                //todo
                .password("[ENCODED]")
                .active(false)
                .build();
        this.userRepository.save(user);
        return new RegistrationResult(true, RegistrationResult.RequiredAction.CONFIRM_EMAIL);
    }
}
