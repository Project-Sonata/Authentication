package com.odeyalo.sonata.authentication.service.password;

import com.odeyalo.sonata.common.shared.ErrorDetails;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class PasswordUpdatingResult {
    boolean updated;
    ErrorDetails errorDetails;

    public static PasswordUpdatingResult updated() {
        return of(true, null);
    }

    public static PasswordUpdatingResult failed(ErrorDetails errorDetails) {
        return of(false, errorDetails);
    }
}
