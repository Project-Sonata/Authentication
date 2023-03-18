package com.odeyalo.sonata.authentication.testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Class to create a fakes of {@link UserRegistrationInfo}
 */
@Getter
@ToString
public class UserRegistrationInfoFaker {
    private String email;
    private String password;
    private String gender;
    private LocalDate birthdate;
    private boolean notificationEnabled;

    @Getter(value = AccessLevel.NONE)
    private Faker faker = new Faker();
    @ToString.Exclude
    @Getter(value = AccessLevel.NONE)
    private final Logger logger = LoggerFactory.getLogger(UserRegistrationInfoFaker.class);

    public UserRegistrationInfoFaker() {
        randomUserRegistrationInfo();
    }

    public UserRegistrationInfoFaker(Faker faker) {
        this.faker = faker;
        randomUserRegistrationInfo();
    }

    public static UserRegistrationInfoFaker create() {
        return new UserRegistrationInfoFaker();
    }

    public static UserRegistrationInfoFaker withFaker(Faker faker) {
        return new UserRegistrationInfoFaker(faker);
    }


    public UserRegistrationInfoFaker overrideEmail(String email) {
        this.email = email;
        return this;
    }

    public UserRegistrationInfoFaker overridePassword(String password) {
        this.password = password;
        return this;
    }

    public UserRegistrationInfoFaker overrideGender(String gender) {
        this.gender = gender;
        return this;
    }

    public UserRegistrationInfoFaker overrideBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public UserRegistrationInfoFaker overrideNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
        return this;
    }


    public UserRegistrationInfo get() {
        UserRegistrationInfo info = UserRegistrationInfo
                .builder()
                .email(email)
                .password(password)
                .gender(gender)
                .birthdate(birthdate)
                .notificationEnabled(notificationEnabled)
                .build();
        this.logger.debug("Created: {}", info);
        return info;
    }

    private void randomUserRegistrationInfo() {
        this.email = faker.internet().emailAddress();
        this.password = faker.internet().password(8, 32);
        this.gender = randomGender();
        this.birthdate = toLocalDate(faker.date().birthday(14, 100));
        this.notificationEnabled = faker.bool().bool();
    }

    private LocalDate toLocalDate(Date date) {
        return DateUtils.toCalendar(date).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private String randomGender() {
        Integer genderCode = faker.random().nextInt(0, 2);
        if (genderCode == 0) {
            return "MALE";
        }
        if (genderCode == 1) {
            return "FEMALE";
        }
        if (genderCode == 2) {
            return "NONE";
        }
        return null;
    }
}
