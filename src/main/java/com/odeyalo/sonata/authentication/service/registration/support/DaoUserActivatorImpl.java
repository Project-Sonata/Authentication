package com.odeyalo.sonata.authentication.service.registration.support;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Activate the user through repository
 */
@Service
public class DaoUserActivatorImpl implements UserActivator {
    private final UserRepository userRepository;

    public DaoUserActivatorImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void activateUser(User user) {
        user.setActive(true);
        userRepository.save(user);
    }
}
