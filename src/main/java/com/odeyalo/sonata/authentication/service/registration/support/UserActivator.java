package com.odeyalo.sonata.authentication.service.registration.support;

import com.odeyalo.sonata.authentication.entity.User;

/**
 * Make user active.
 */
public interface UserActivator {
    /**
     * Activate the user if user was not activated, do nothing if the user is activated
     * @param user - user to activate
     */
    void activateUser(User user);
}
