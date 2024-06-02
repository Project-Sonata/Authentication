package com.odeyalo.sonata.authentication.testing.faker;

import com.github.javafaker.Faker;
import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.entity.settings.UserSettings;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.function.Consumer;

public class UserFaker {
    private Long id;
    private String sonataId;
    private String email;
    private String password;
    private boolean active;
    private UserSettings userSettings;
    private Faker faker = new Faker();

    public UserFaker() {
        initializeFakedValues();
    }

    public UserFaker(Faker faker) {
        this.faker = faker;
        initializeFakedValues();
    }

    public UserFaker(Long id, String email, String password, boolean active, String sonataId) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.active = active;
        this.sonataId = sonataId;
    }

    private void initializeFakedValues() {
        this.id = faker.random().nextLong();
        this.email = faker.internet().emailAddress();
        this.password = faker.internet().password(8, 32);
        this.active = faker.random().nextBoolean();
        this.sonataId = RandomStringUtils.randomAlphanumeric(22);
    }

    public static UserFaker create() {
        String email = new Faker().internet().emailAddress();
        return from(RandomUtils.nextLong(), email, RandomStringUtils.random(32), RandomUtils.nextBoolean());
    }

    public static UserFaker withEmail(String email) {
        return from(RandomUtils.nextLong(), email, RandomStringUtils.random(32), RandomUtils.nextBoolean());
    }

    public static UserFaker from(Long id, String email, String password, boolean active) {
        return new UserFaker(id, email, password, active, RandomStringUtils.randomAlphanumeric(22));
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

    public UserFaker overrideUserSettings(UserSettings settings) {
        this.userSettings = settings;
        return this;
    }

    public User get() {
        User user = User.builder()
                .id(id)
                .sonataId(sonataId)
                .email(email)
                .password(password)
                .userSettings(userSettings)
                .active(active)
                .build();

        if (userSettings == null) {
            UserSettings emptySettings = UserSettings.empty(user);
            user.setUserSettings(emptySettings);
        }
        return user;
    }

    /**
     * Build the user and modifies the UserSettings
     * @param modifier - modifier to customize UserSettings
     * @return - user entity
     */
    public User get(Consumer<UserSettings> modifier) {
        User user = get();
        if (userSettings == null) {
            userSettings = UserSettings.empty(user);
        }
        modifier.accept(userSettings);
        user.setUserSettings(userSettings);
        return user;
    }
}
