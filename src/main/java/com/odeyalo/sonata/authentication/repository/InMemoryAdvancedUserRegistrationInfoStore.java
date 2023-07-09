package com.odeyalo.sonata.authentication.repository;

import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;
import com.odeyalo.sonata.common.authentication.dto.request.UserRegistrationInfo;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryAdvancedUserRegistrationInfoStore implements AdvancedUserRegistrationInfoStore {
    private final Map<String, AdvancedUserRegistrationInfo> store = new ConcurrentHashMap<>();

    @Override
    public void save(AdvancedUserRegistrationInfo info) {
        store.put(info.getEmail(), info);
    }

    @Override
    public Optional<AdvancedUserRegistrationInfo> findByEmail(String email) {
        return Optional.ofNullable(store.get(email));
    }
}
