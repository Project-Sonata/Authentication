package com.odeyalo.sonata.authentication;

import com.odeyalo.sonata.authentication.support.event.handler.CompositeUserRegisteredEventHandler;
import com.odeyalo.sonata.authentication.support.event.handler.UserRegisteredEventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class EventWrapperConfiguration {


    @Bean
    @Primary
    public UserRegisteredEventHandler userRegisteredEventHandler(List<UserRegisteredEventHandler> handlers) {
        return new CompositeUserRegisteredEventHandler(handlers);
    }

}
