package com.odeyalo.sonata.authentication.service.login;

import com.odeyalo.sonata.authentication.common.AuthenticationResult;
import com.odeyalo.sonata.authentication.common.LoginCredentials;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Default {@link AuthenticationManager} implementation that just authenticate the users through
 * {@link UserRepository}
 */
@Service
public class DefaultAuthenticationManager implements AuthenticationManager {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DefaultAuthenticationManager(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthenticationResult authenticate(LoginCredentials loginCredentials) {
        User user = userRepository.findUserByEmail(loginCredentials.getEmail());
        if (user == null || !(passwordEncoder.matches(loginCredentials.getPassword(), user.getPassword()))) {
            return AuthenticationResult.failed();
        }
        return AuthenticationResult.success(user);
    }
}
