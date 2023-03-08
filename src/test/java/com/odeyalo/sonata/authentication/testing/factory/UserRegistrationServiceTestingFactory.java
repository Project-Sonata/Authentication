package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.InMemoryUserRepository;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.authentication.service.registration.EmailConfirmationUserRegistrationService;
import com.odeyalo.sonata.authentication.service.registration.UserRegistrationService;

import java.util.function.Consumer;

public class UserRegistrationServiceTestingFactory {

    public static DefaultUserRegistrationServiceBuilder createDefaultService() {
        return new DefaultUserRegistrationServiceBuilder();
    }

    public static UserRegistrationService create() {
        return createDefaultService().build();
    }

    public static UserRegistrationService create(Consumer<UserRegistrationService> modifier) {
        UserRegistrationService parent = create();

        modifier.accept(parent);

        return parent;
    }

    public static class DefaultUserRegistrationServiceBuilder {
        private UserRepository userRepository = new InMemoryUserRepository();

        public DefaultUserRegistrationServiceBuilder overrideUserRepository(UserRepository userRepository) {
            this.userRepository = userRepository;
            return this;
        }

        public DefaultUserRegistrationServiceBuilder overrideUserRepositoryUsers(User... users) {
            for (User user : users) {
                userRepository.save(user);
            }
            return this;
        }

        public EmailConfirmationUserRegistrationService build() {
            return new EmailConfirmationUserRegistrationService(userRepository);
        }
    }
}
