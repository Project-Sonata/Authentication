package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.authentication.service.registration.support.DaoUserActivatorImpl;
import com.odeyalo.sonata.authentication.service.registration.support.UserActivator;
import lombok.Getter;

/**
 * Factory to create a new {@link UserActivator} for tests
 */
public class UserActivatorTestingFactory {

    public static UserActivator create() {
        return createDaoImpl();
    }

    public static DaoUserActivatorImplBuilder createDaoImplBuilder() {
        return new DaoUserActivatorImplBuilder();
    }

    public static DaoUserActivatorImpl createDaoImpl() {
        return new DaoUserActivatorImplBuilder().build();
    }

    @Getter
    public static class DaoUserActivatorImplBuilder {
        private UserRepository repository = UserRepositoryTestingFactory.create();

        public DaoUserActivatorImplBuilder overrideRepository(UserRepository repository) {
            this.repository = repository;
            return this;
        }
        public DaoUserActivatorImpl build() {
            return new DaoUserActivatorImpl(repository);
        }
    }
}
