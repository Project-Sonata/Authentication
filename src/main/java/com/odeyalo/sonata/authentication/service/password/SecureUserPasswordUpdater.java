package com.odeyalo.sonata.authentication.service.password;

public interface SecureUserPasswordUpdater {

    PasswordUpdatingResult updatePassword(long userId, PasswordContainer container);

}
