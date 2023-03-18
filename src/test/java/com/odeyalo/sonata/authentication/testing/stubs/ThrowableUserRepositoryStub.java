package com.odeyalo.sonata.authentication.testing.stubs;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;

import java.util.Optional;

/**
 * Simple {@link UserRepository} stub that throws exception in {@link #save(User)} method and do nothing in other methods
 */
public class ThrowableUserRepositoryStub implements UserRepository {

    @Override
    public User findUserByEmail(String email) {
        return null;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public <T extends User> T save(User user) {
        throw new RuntimeException("The user with email: " + user.getEmail() + " already exist");
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public long count() {
        return 0;
    }
}
