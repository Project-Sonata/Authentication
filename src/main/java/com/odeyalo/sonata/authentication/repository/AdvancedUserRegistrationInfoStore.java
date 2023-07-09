package com.odeyalo.sonata.authentication.repository;

import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;
import com.odeyalo.sonata.common.authentication.dto.request.UserRegistrationInfo;

import java.util.Optional;

public interface AdvancedUserRegistrationInfoStore {
    void save(AdvancedUserRegistrationInfo info);

    Optional<AdvancedUserRegistrationInfo> findByEmail(String email);
}
