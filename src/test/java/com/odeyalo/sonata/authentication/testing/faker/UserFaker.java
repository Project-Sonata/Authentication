package com.odeyalo.sonata.authentication.testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.authentication.entity.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

public class UserFaker {
    private Long id;
    private String email;
    private String password;
    private boolean active;
    private Faker faker = new Faker();

    public UserFaker() {
        initializeFakedValues();
    }

    public UserFaker(Faker faker) {
        this.faker = faker;
        initializeFakedValues();
    }

    public UserFaker(Long id, String email, String password, boolean active) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.active = active;
    }

    private void initializeFakedValues() {
        this.id = faker.random().nextLong();
        this.email = faker.internet().emailAddress();
        this.password = faker.internet().password(8, 32);
        this.active = faker.random().nextBoolean();
    }

    public static UserFaker create() {
        String email = new Faker().internet().emailAddress();
        return from(RandomUtils.nextLong(), email, RandomStringUtils.random(32), RandomUtils.nextBoolean());
    }

    public static UserFaker withEmail(String email) {
        return from(RandomUtils.nextLong(), email, RandomStringUtils.random(32), RandomUtils.nextBoolean());
    }

    public static UserFaker from(Long id, String email, String password, boolean active) {
        return new UserFaker(id, email, password, active);
    }

    public UserFaker overrideId(Long id) {
        this.id = id;
        return this;
    }

    public UserFaker overrideEmail(String email) {
        this.email = email;
        return this;
    }

    public UserFaker overridePassword(String password) {
        this.password = password;
        return this;
    }

    public UserFaker makeActive() {
        this.active = true;
        return this;
    }

    public UserFaker makeInactive() {
        this.active = false;
        return this;
    }

    public User get() {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .active(active)
                .build();
    }
}
