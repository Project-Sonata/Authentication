package com.odeyalo.sonata.authentication.service.registration;


import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;

public interface UserRegistrationManager {
    /**
     * Register the user in the system
     * @param info - provided info by user
     */
    RegistrationResult registerUser(AdvancedUserRegistrationInfo info);
}
