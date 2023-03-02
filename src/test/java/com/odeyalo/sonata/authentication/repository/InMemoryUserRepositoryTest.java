package com.odeyalo.sonata.authentication.repository;

import com.odeyalo.sonata.authentication.entity.User;
import com.odeyalo.sonata.authentication.testing.factory.UserEntityTestingFactory;
import com.odeyalo.sonata.authentication.testing.factory.UserRepositoryTestingFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contains tests for {@link InMemoryUserRepository} class.
 */
class InMemoryUserRepositoryTest {

    @Test
    @DisplayName("Create new instance with null List and expect IllegalArgumentException")
    void createInMemoryUserRepositoryWithNullList_andExpectException() {
        assertThrows(IllegalArgumentException.class, () -> new InMemoryUserRepository((List<User>) null));
    }

    @Test
    @DisplayName("Find existing user in repository by email and expect correct user")
    void findExistingUserInRepositoryByEmail_andExpectSuccess() {
        // Given
        User expectedUser = UserEntityTestingFactory.createValid();
        String existingEmail = expectedUser.getEmail();

        InMemoryUserRepository repository = UserRepositoryTestingFactory.inMemoryWithUsers(
                expectedUser,
                UserEntityTestingFactory.createAndModify((user) -> user.setActive(false))
        );
        long before = repository.count();
        // When
        User actualUser = repository.findUserByEmail(existingEmail);

        // Then
        long after = repository.count();
        assertEquals(before, after, "If findUserByEmail method was called, then User should be only returned and NOT be deleted from repository");
        assertEquals(expectedUser, actualUser, "If a user presented in the repository, then user should be found");
    }

    @Test
    @DisplayName("Find not existing user in repository by email and expect null as result")
    void findNotExistingUserInRepositoryByEmail_andExpectNull() {
        // Given
        String notExistingEmail = "miku@gmail.com";
        InMemoryUserRepository repository = UserRepositoryTestingFactory.inMemoryWithUsers(
                UserEntityTestingFactory.createValid(),
                UserEntityTestingFactory.createAndModify((user) -> user.setActive(false))
        );

        long before = repository.count();
        // When
        User actualUser = repository.findUserByEmail(notExistingEmail);
        // Then
        long after = repository.count();
        assertEquals(before, after, "If findUserByEmail method was called, then User should be only returned and NOT be deleted from repository");
        assertNull(actualUser, "If the user not presented in the repository, then null must be returned as result!");
    }

    @Test
    @DisplayName("Find user by existing id and expect user as result")
    void findByExistingId_andExpectCorrectUser() {
        // Given
        User firstUser = UserEntityTestingFactory.createValid();
        User secondUser = UserEntityTestingFactory.createAndModify((user) -> user.setActive(false));
        User thirdUser = UserEntityTestingFactory.createValid();

        InMemoryUserRepository repository = UserRepositoryTestingFactory.inMemoryWithUsers(firstUser, secondUser, thirdUser);
        long sizeBefore = repository.count();
        // When
        Optional<User> actualFirstUser = repository.findById(firstUser.getId());
        Optional<User> actualSecondUser = repository.findById(secondUser.getId());
        Optional<User> actualThirdUser = repository.findById(thirdUser.getId());

        // Then
        long sizeAfter = repository.count();
        assertEquals(sizeBefore, sizeAfter, "findById() method MUST NOT affect the size of the users and just return user by id!");
        assertEquals(firstUser, actualFirstUser.get(), "The user must be associated with his id!");
        assertEquals(secondUser, actualSecondUser.get(), "The user must be associated with his id!");
        assertEquals(thirdUser, actualThirdUser.get(), "The user must be associated with his id!");
    }

    @Test
    @DisplayName("Find user by not existing id and expect null as result")
    void findByNotExistingId_andExpectNull() {
        // Given
        User firstUser = UserEntityTestingFactory.createValid();
        User secondUser = UserEntityTestingFactory.createAndModify((user) -> user.setActive(false));
        User thirdUser = UserEntityTestingFactory.createValid();

        InMemoryUserRepository repository = UserRepositoryTestingFactory.inMemoryWithUsers(firstUser, secondUser, thirdUser);
        long sizeBefore = repository.count();
        // When
        Optional<User> actualUser = repository.findById(10L);

        // Then
        long sizeAfter = repository.count();
        assertEquals(sizeBefore, sizeAfter, "findById() method MUST NOT affect the size of the users and just return user by id!");
        assertFalse(actualUser.isPresent(), "If ID does not exist in the repository, then null must be returned!");
    }

    @Test
    @DisplayName("Count users in empty repository and expect 0")
    void countUsersInEmptyRepo_andExpect0() {
        InMemoryUserRepository emptyRepo = UserRepositoryTestingFactory.inMemoryCreate();

        long count = emptyRepo.count();
        assertEquals(0, count, "If repository does not contain any users, then 0 must be returned!");
    }

    @Test
    @DisplayName("Count users in non-empty repository and expect number of users")
    void countUsersInRepo_andExpectUsersNumber() {
        User firstUser = UserEntityTestingFactory.createValid();
        User secondUser = UserEntityTestingFactory.createAndModify((user) -> user.setActive(false));
        User thirdUser = UserEntityTestingFactory.createValid();

        InMemoryUserRepository repository = UserRepositoryTestingFactory.inMemoryWithUsers(firstUser, secondUser, thirdUser);

        long count = repository.count();

        assertEquals(3, count, "If repository contains users, then number of users in repo must be returned!");
    }

    @Test
    @DisplayName("Save the user inside the repository and expect user to be saved")
    void saveUser_andExpectUserToBeSaved() {
        // Given
        User userToSave = UserEntityTestingFactory.createValid();
        InMemoryUserRepository repository = UserRepositoryTestingFactory.inMemoryCreate();
        // When
        User user = repository.save(userToSave);
        // Then
        assertNotNull(user, "save() method should return non-null user!");
        assertNotNull(user.getId(), "save() method method should set user's id if ID was not presented!");
        assertEquals(user, userToSave, "save() method must return same user!");

        Optional<User> byId = repository.findById(user.getId());

        assertTrue(byId.isPresent(), "Save method must return saved User entity!");
        assertEquals(user, byId.get(), "If save() method was called, then user must be saved!");
    }

    @Test
    @DisplayName("Save the user with null as ID in the repository and expect user to be saved with auto-generated ID")
    void saveUserWithNullId_andExpectUserToBeSaved() {
        User userToSave = UserEntityTestingFactory.createAndModify((user) -> user.setId(null));
        InMemoryUserRepository repository = UserRepositoryTestingFactory.inMemoryCreate();

        User user = repository.save(userToSave);

        assertNotNull(user, "save() method should return non-null user!");
        assertNotNull(user.getId(), "save() method method should set user's id if ID was not presented!");

        Optional<User> byId = repository.findById(user.getId());

        assertTrue(byId.isPresent(), "Save method must return saved User entity!");
        assertEquals(user, byId.get(), "If save() method was called, then user must be saved!");
    }

    @Test
    @DisplayName("Delete existing user by correct id and expect user to be deleted")
    void deleteExistingUserById_andExpectUserToBeDeleted() {
        // Given
        User firstUser = UserEntityTestingFactory.createValid();
        User secondUser = UserEntityTestingFactory.createAndModify((user) -> user.setActive(false));
        User thirdUser = UserEntityTestingFactory.createValid();

        InMemoryUserRepository repository = UserRepositoryTestingFactory.inMemoryWithUsers(firstUser, secondUser, thirdUser);
        long countBefore = repository.count();

        // When
        repository.deleteById(firstUser.getId());
        // Then
        long countAfter = repository.count();

        assertEquals(countBefore - 1, countAfter, "If the user was deleted from repository, then count must be decremented!");

        Optional<User> user = repository.findById(firstUser.getId());
        assertFalse(user.isPresent(), "If the user was removed from repo, then repository MUST NOT contain user anymore");
    }

    @Test
    @DisplayName("Delete existing user by correct id and expect user to be deleted")
    void deleteNotExistingUserById_andExpectNothingToBeDeleted() {
        // Given
        User firstUser = UserEntityTestingFactory.createValid();
        User secondUser = UserEntityTestingFactory.createAndModify((user) -> user.setActive(false));
        User thirdUser = UserEntityTestingFactory.createValid();

        InMemoryUserRepository repository = UserRepositoryTestingFactory.inMemoryWithUsers(firstUser, secondUser, thirdUser);
        long countBefore = repository.count();

        // When
        repository.deleteById(100L);
        // Then
        long countAfter = repository.count();

        assertEquals(countBefore, countAfter, "If ID does not exist in the repo, then nothing must be deleted!");

        Optional<User> actualFirstUser = repository.findById(firstUser.getId());
        Optional<User> actualSecondUser = repository.findById(secondUser.getId());
        Optional<User> actualThirdUser = repository.findById(thirdUser.getId());


        assertTrue(actualFirstUser.isPresent(), "If nothing was deleted from repo, then user must be not null!");
        assertTrue(actualSecondUser.isPresent(), "If nothing was deleted from repo, then user must be not null!");
        assertTrue(actualThirdUser.isPresent(), "If nothing was deleted from repo, then user must be not null!");
    }
}
