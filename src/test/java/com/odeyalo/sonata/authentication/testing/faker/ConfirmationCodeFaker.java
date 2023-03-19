package com.odeyalo.sonata.authentication.testing.faker;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import lombok.ToString;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Simple Faker to create a new {@link ConfirmationCode} for testing purposes
 */
@ToString
public class ConfirmationCodeFaker {
    private Long id;
    private String codeValue;
    private LocalDateTime createdAt;
    private LocalDateTime expiresIn;
    private boolean activated;
    private ConfirmationCode.LifecycleStage lifecycleStage;

    @ToString.Exclude
    private final Logger logger = LoggerFactory.getLogger(ConfirmationCodeFaker.class);

    public ConfirmationCodeFaker(Long id, String codeValue, boolean activated, ConfirmationCode.LifecycleStage lifecycleStage) {
        this.id = id;
        this.codeValue = codeValue;
        this.activated = activated;
        this.lifecycleStage = lifecycleStage;
        this.createdAt = LocalDateTime.now();
        this.expiresIn = createdAt.plusMinutes(10);
    }

    public ConfirmationCodeFaker(Long id, String codeValue, LocalDateTime createdAt, LocalDateTime expiresIn, boolean activated, ConfirmationCode.LifecycleStage lifecycleStage) {
        this.id = id;
        this.codeValue = codeValue;
        this.createdAt = createdAt;
        this.expiresIn = expiresIn;
        this.activated = activated;
        this.lifecycleStage = lifecycleStage;
    }

    public static ConfirmationCodeFaker withBody(String body) {
        return withBodyNonActivated(body);
    }

    public static ConfirmationCodeFaker numeric() {
        return withBodyNonActivated(RandomStringUtils.randomNumeric(8));
    }

    public static ConfirmationCodeFaker alphanumeric() {
        return withBodyNonActivated(RandomStringUtils.randomAlphanumeric(8));
    }

    public static ConfirmationCodeFaker onlyLetters() {
        return withBodyNonActivated(RandomStringUtils.randomAlphabetic(8));
    }

    public static ConfirmationCodeFaker withBodyNonActivated(String body) {
        return new ConfirmationCodeFaker(RandomUtils.nextLong(), body, false, ConfirmationCode.LifecycleStage.CREATED);
    }

    public static ConfirmationCodeFaker expired() {
        String body = RandomStringUtils.randomNumeric(8);
        LocalDateTime createdAt = LocalDateTime.of(2022, 3, 20, 13, 30);
        LocalDateTime expiresIn = LocalDateTime.of(2022, 3, 20, 13, 40);
        return new ConfirmationCodeFaker(RandomUtils.nextLong(), body, createdAt, expiresIn, false, ConfirmationCode.LifecycleStage.CREATED);
    }

    public ConfirmationCodeFaker nullId() {
        this.id = null;
        return this;
    }

    public ConfirmationCodeFaker overrideId(Long id) {
        this.id = id;
        return this;
    }

    public ConfirmationCodeFaker randomId() {
        this.id = RandomUtils.nextLong();
        return this;
    }

    public ConfirmationCodeFaker codeValue(String codeValue) {
        this.codeValue = codeValue;
        return this;
    }

    public ConfirmationCodeFaker atSpecificLifecycle(ConfirmationCode.LifecycleStage stage) {
        this.lifecycleStage = stage;
        return this;
    }

    public ConfirmationCodeFaker activated() {
        this.activated = true;
        return this;
    }

    public ConfirmationCodeFaker deactivated() {
        this.activated = false;
        return this;
    }

    public ConfirmationCodeFaker lifetime(Integer lifetimeMinutes) {
        this.createdAt = LocalDateTime.now();
        this.expiresIn = createdAt.plusMinutes(lifetimeMinutes);
        return this;
    }

    public ConfirmationCode get() {
        ConfirmationCode confirmationCode = ConfirmationCode
                .builder()
                .id(id)
                .codeValue(codeValue)
                .createdAt(createdAt)
                .expirationTime(expiresIn)
                .activated(activated)
                .lifecycleStage(lifecycleStage)
                .build();
        this.logger.debug("Created fake confirmation code: {}", confirmationCode);
        return confirmationCode;
    }
}
