package com.odeyalo.sonata.authentication.service.confirmation;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.entity.User;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;

/**
 * {@link ConfirmationCodeGenerator} implementation that generates confirmation code with only digits
 */
public class NumericConfirmationCodeGenerator implements ConfirmationCodeGenerator {

    @Override
    public ConfirmationCode generateCode(User user, int length, int lifetimeMinutes) {
        String codeValue = RandomStringUtils.randomNumeric(length);
        LocalDateTime createdAt = LocalDateTime.now();

        return ConfirmationCode
                .builder()
                .createdAt(createdAt)
                .expirationTime(createdAt.plusMinutes(lifetimeMinutes))
                .codeValue(codeValue)
                .user(user)
                .build();
    }
}
