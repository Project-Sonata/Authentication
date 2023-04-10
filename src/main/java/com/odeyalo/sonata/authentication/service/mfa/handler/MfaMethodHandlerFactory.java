package com.odeyalo.sonata.authentication.service.mfa.handler;

import com.odeyalo.sonata.authentication.entity.settings.UserMfaSettings;

/**
 * Factory to produce the {@link MfaMethodHandler} by Mfa type
 */
public interface MfaMethodHandlerFactory {

    default MfaMethodHandler getHandler(UserMfaSettings.MfaType mfaType) {
        return getHandler(mfaType.name().toLowerCase());
    }

    MfaMethodHandler getHandler(String type);
}
