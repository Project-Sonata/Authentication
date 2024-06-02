package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.testing.faker.UserFaker;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Factory to create {@link User} entity instance for testing.
 */
public class UserEntityTestingFactory {
    private static final AtomicLong idHolder = new AtomicLong();

    public static User createValid() {
        String existingEmail = "odeyalo@gmail.com";

        return UserFaker.create().overrideId(idHolder.incrementAndGet())
                .overrideEmail(existingEmail)
                .get();
    }

    public static User createAndModify(Consumer<User> modifier) {
        User user = createValid();

        modifier.accept(user);

        return user;
    }
}
