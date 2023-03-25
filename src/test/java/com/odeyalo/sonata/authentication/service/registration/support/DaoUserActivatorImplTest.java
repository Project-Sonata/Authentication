package com.odeyalo.sonata.authentication.service.registration.support;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.authentication.testing.factory.UserActivatorTestingFactory;
import com.odeyalo.sonata.authentication.testing.faker.UserFaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DaoUserActivatorImplTest {

    @Test
    @DisplayName("Activate non-active user and expect user to be activated")
    void activateNonActiveUser_andExpectUserToBeActivated() {
        // given
        User user = UserFaker.create().makeInactive().get();
        UserActivatorTestingFactory.DaoUserActivatorImplBuilder builder = UserActivatorTestingFactory.createDaoImplBuilder();
        UserRepository repo = builder.getRepository();
        DaoUserActivatorImpl userActivator = builder.build();
        // when
        userActivator.activateUser(user);
        // then
        Optional<User> optional = repo.findById(user.getId());

        assertThat(optional)
                .as("User must be presented!")
                .isPresent();

        User actualUser = optional.get();

        assertThat(actualUser.isActive())
                .as("The user should be activated!")
                .isTrue();
    }

    @Test
    @DisplayName("Activate active user and expect nothing to happen")
    void activateActiveUser_andExpectNothingToHappen() {
        // given
        User user = UserFaker.create().makeActive().get();
        UserActivatorTestingFactory.DaoUserActivatorImplBuilder builder = UserActivatorTestingFactory.createDaoImplBuilder();
        UserRepository repo = builder.getRepository();
        DaoUserActivatorImpl userActivator = builder.build();
        // when
        userActivator.activateUser(user);
        // then
        Optional<User> optional = repo.findById(user.getId());
        assertThat(optional)
                .as("User entity must be not affected!")
                .isPresent()
                .hasValue(user);
    }
}
