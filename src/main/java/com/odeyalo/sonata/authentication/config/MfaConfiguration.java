package com.odeyalo.sonata.authentication.config;

import com.odeyalo.sonata.authentication.repository.UserRepository;
import com.odeyalo.sonata.authentication.service.login.AuthenticationManager;
import com.odeyalo.sonata.authentication.service.login.DefaultAuthenticationManager;
import com.odeyalo.sonata.authentication.service.login.mfa.AdditionalAuthenticationRequirementProvider;
import com.odeyalo.sonata.authentication.service.login.mfa.CompositeAdditionalAuthenticationRequirementProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class MfaConfiguration {

    @Bean
    public CompositeAdditionalAuthenticationRequirementProvider compositeAdditionalAuthenticationRequirementProvider(List<AdditionalAuthenticationRequirementProvider> providers) {
        return new CompositeAdditionalAuthenticationRequirementProvider(providers);
    }

    @Bean
    public AuthenticationManager authenticationManager(UserRepository userRepository,
                                                       PasswordEncoder passwordEncoder,
                                                       @Qualifier("compositeAdditionalAuthenticationRequirementProvider") AdditionalAuthenticationRequirementProvider provider) {
        return new DefaultAuthenticationManager(userRepository, passwordEncoder, provider);
    }
}
