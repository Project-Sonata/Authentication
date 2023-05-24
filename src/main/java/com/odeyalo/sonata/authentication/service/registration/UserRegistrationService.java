package com.odeyalo.sonata.authentication.service.registration;

import com.odeyalo.sonata.authentication.dto.request.AdvancedUserRegistrationInfo;

public interface UserRegistrationService {
    /**
     * Register the user in the Sonata Application
     * @param info - provided registration info by user
     * @return - {@link RegistrationResult#success()} if user was registered, {@link RegistrationResult#failed(RegistrationResult.RequiredAction)} otherwise
     */
    RegistrationResult registerUser(AdvancedUserRegistrationInfo info);
}
