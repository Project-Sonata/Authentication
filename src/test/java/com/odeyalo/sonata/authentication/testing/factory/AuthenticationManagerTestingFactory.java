package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.authentication.service.login.AuthenticationService;
import com.odeyalo.sonata.authentication.service.login.DefaultAuthenticationService;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Factory to create a new {@link AuthenticationService} in test environment
 */
public class AuthenticationManagerTestingFactory {

    public static AuthenticationService create() {
        return createDefault();
    }

    public static DefaultAuthenticationService createDefault() {
        return createDefaultBuilder().build();
    }


    public static DefaultAuthenticationManagerBuilder createDefaultBuilder() {
        return new DefaultAuthenticationManagerBuilder();
    }


    @Getter
    public static class DefaultAuthenticationManagerBuilder {
        private UserRepository userRepository = UserRepositoryTestingFactory.create();
        private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        public DefaultAuthenticationManagerBuilder overrideUserRepository(UserRepository userRepository) {
            this.userRepository = userRepository;
            return this;
        }

        public DefaultAuthenticationManagerBuilder overridePasswordEncoder(PasswordEncoder passwordEncoder) {
            this.passwordEncoder = passwordEncoder;
            return this;
        }

        public DefaultAuthenticationManagerBuilder withPredefinedUsers(User... users) {
            for (User user : users) {
                userRepository.save(user);
            }
            return this;
        }

        public DefaultAuthenticationService build() {
            return new DefaultAuthenticationService(userRepository, passwordEncoder);
        }
    }
}
