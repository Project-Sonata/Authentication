package com.odeyalo.sonata.authentication.repository;

import com.odeyalo.sonata.authentication.entity.User;

import java.util.Optional;

/**
 * User repository that provides basic CRUD operations and additional methods for working with User entity
 */
public interface UserRepository {

    User findUserByEmail(String email);

    Optional<User> findById(Long id);

    <T extends User> T save(User user);

    void deleteById(Long id);

    long count();
}
