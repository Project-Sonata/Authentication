package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.InMemoryUserRepository;
import com.odeyalo.sonata.authentication.repository.UserRepository;

public class UserRepositoryTestingFactory {

    public static InMemoryUserRepository inMemoryCreate() {
        return new InMemoryUserRepository();
    }

    public static InMemoryUserRepository inMemoryWithUsers(User... users) {
        return new InMemoryUserRepository(users);
    }

    public static UserRepository create() {
        return new InMemoryUserRepository();
    }

    public static UserRepository withUsers(User... users) {
        return new InMemoryUserRepository(users);
    }
}
