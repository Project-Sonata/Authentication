package com.odeyalo.sonata.authentication.repository;

import com.odeyalo.sonata.authentication.entity.User;

import java.util.Optional;

/**
 * User repository that provides basic CRUD operations and additional methods for working with User entity
 */
public interface UserRepository {
    /**
     * Find the user by email
     * Never throws any exception
     * @param email - user's email to search
     * @return - found user or null
     *
     */
    User findUserByEmail(String email);

    /**
     * Find the user by id
     * @param id - user's id to search for
     * @return - {@link User} wrapped in Optional, or {@link Optional#empty()}
     */
    Optional<User> findById(Long id);

    /**
     * Save the user in repository
     * Can throw exception if any exception has be occurred and saving is not possible
     * @param user - user to save
     * @param <T> - type of the user
     * @return - saved user
     */
    <T extends User> T save(User user);

    /**
     * Delete user by id.
     * If the user with id does not exist, do nothing
     * @param id - user's id to delete the user
     */
    void deleteById(Long id);

    /**
     * Return number of users in repository
     * @return - number of users
     */
    long count();
}
