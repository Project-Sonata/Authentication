package com.odeyalo.sonata.authentication.service.registration;

import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;

public class DefaultUserRegistrationService implements UserRegistrationService {
    private final UserRepository userRepository;

    public DefaultUserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public RegistrationResult registerUser(UserRegistrationInfo info) {
        User user = User
                .builder()
                .email(info.getEmail())
                .active(false)
                .build();
        this.userRepository.save(user);
        return new RegistrationResult(true, RegistrationResult.RequiredAction.CONFIRM_EMAIL);
    }
}
