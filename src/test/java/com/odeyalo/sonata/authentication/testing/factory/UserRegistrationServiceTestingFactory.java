package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.InMemoryAdvancedUserRegistrationInfoStore;
import com.odeyalo.sonata.authentication.repository.InMemoryUserRepository;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.authentication.service.confirmation.EmailConfirmationCodeGeneratorSender;
import com.odeyalo.sonata.authentication.service.registration.EmailConfirmationUserRegistrationService;
import com.odeyalo.sonata.authentication.service.registration.UserRegistrationService;
import com.odeyalo.sonata.authentication.testing.spy.EmptyEmailConfirmationCodeGeneratorSenderSpy;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.function.Consumer;

public class UserRegistrationServiceTestingFactory {

    public static EmailConfirmationUserRegistrationServiceBuilder createEmailService() {
        return new EmailConfirmationUserRegistrationServiceBuilder();
    }

    public static UserRegistrationService create() {
        return createEmailService().build();
    }

    public static UserRegistrationService create(Consumer<UserRegistrationService> modifier) {
        UserRegistrationService parent = create();

        modifier.accept(parent);

        return parent;
    }

    @Getter
    public static class EmailConfirmationUserRegistrationServiceBuilder {
        private UserRepository userRepository = new InMemoryUserRepository();
        private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        private EmailConfirmationCodeGeneratorSender sender = new EmptyEmailConfirmationCodeGeneratorSenderSpy();

        public EmailConfirmationUserRegistrationServiceBuilder overrideUserRepository(UserRepository userRepository) {
            this.userRepository = userRepository;
            return this;
        }

        public EmailConfirmationUserRegistrationServiceBuilder overrideUserRepositoryUsers(User... users) {
            for (User user : users) {
                userRepository.save(user);
            }
            return this;
        }

        public EmailConfirmationUserRegistrationServiceBuilder overridePasswordEncoder(PasswordEncoder passwordEncoder) {
            this.passwordEncoder = passwordEncoder;
            return this;
        }

        public EmailConfirmationUserRegistrationServiceBuilder overrideEmailConfirmationCodeGeneratorSender(EmailConfirmationCodeGeneratorSender sender) {
            this.sender = sender;
            return this;
        }

        public EmailConfirmationUserRegistrationService build() {
            return new EmailConfirmationUserRegistrationService(userRepository, sender, passwordEncoder);
        }
    }
}
