package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.authentication.service.login.AuthenticationManager;
import com.odeyalo.sonata.authentication.service.login.DefaultAuthenticationManager;
import com.odeyalo.sonata.authentication.service.login.mfa.AdditionalAuthenticationRequirementProvider;
import com.odeyalo.sonata.authentication.service.login.mfa.NoOpAdditionalAuthenticationRequirementProvider;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Factory to create a new {@link AuthenticationManager} in test environment
 */
public class AuthenticationManagerTestingFactory {

    public static AuthenticationManager create() {
        return createDefault();
    }

    public static DefaultAuthenticationManager createDefault() {
        return createDefaultBuilder().build();
    }


    public static DefaultAuthenticationManagerBuilder createDefaultBuilder() {
        return new DefaultAuthenticationManagerBuilder();
    }


    @Getter
    public static class DefaultAuthenticationManagerBuilder {
        private UserRepository userRepository = UserRepositoryTestingFactory.create();
        private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        private AdditionalAuthenticationRequirementProvider additionalAuthenticationRequirementProvider = new NoOpAdditionalAuthenticationRequirementProvider();

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
        public DefaultAuthenticationManagerBuilder overrideAdditionalAuthenticationRequirementProvider(AdditionalAuthenticationRequirementProvider additionalAuthenticationRequirementProvider) {
            this.additionalAuthenticationRequirementProvider = additionalAuthenticationRequirementProvider;
            return this;
        }

        public DefaultAuthenticationManager build() {
            return new DefaultAuthenticationManager(userRepository, passwordEncoder, additionalAuthenticationRequirementProvider);
        }
    }
}
