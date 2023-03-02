package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.entity.User;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Factory to create {@link User} entity instance for testing.
 */
public class UserEntityTestingFactory {
    private static final AtomicLong idHolder = new AtomicLong();

    public static User createValid() {
        String existingEmail = "odeyalo@gmail.com";

        return new User(idHolder.incrementAndGet(), existingEmail, "validpassword123", true);
    }

    public static User createAndModify(Consumer<User> modifier) {
        User user = createValid();

        modifier.accept(user);

        return user;
    }
}
