package com.odeyalo.sonata.authentication.repository;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.testing.factory.UserEntityTestingFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class for {@link JpaSupportUserRepository}
 */
@DataJpaTest
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class JpaSupportUserRepositoryTest {

    @Autowired
    private JpaSupportUserRepository userRepository;
    public static final String EXISTING_EMAIL = "odeyalo@gmail.com";
    public static final String NOT_EXISTING_EMAIL = "king@gmail.com";

    User existingUser = UserEntityTestingFactory.createValid();

    @BeforeAll
    void setUp() {
        ((JpaRepository<User, Long>) userRepository).save(existingUser);
    }


    @Test
    @DisplayName("Find user by existing email and expect same user")
    void findUserByExistingEmail_andExpectNonNullUser() {
        User user = userRepository.findUserByEmail(EXISTING_EMAIL);
        assertEquals(existingUser, user, "Users must be equal if they have same email!");
    }

    @Test
    @DisplayName("Find user be not existing email and expect null as result")
    void findUserByNotExistingEmail_andExpectNull() {
        User user = userRepository.findUserByEmail(NOT_EXISTING_EMAIL);

        assertNull(user, "If repository does not contain user with email, then null must be returned!");
    }
}
