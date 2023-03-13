package com.odeyalo.sonata.authentication.repository;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.testing.factory.ConfirmationCodeRepositoryTestingFactory;
import com.odeyalo.sonata.authentication.testing.faker.ConfirmationCodeFaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link InMemoryConfirmationCodeRepository}
 */
class InMemoryConfirmationCodeRepositoryTest {

    @Test
    @DisplayName("Find the confirmation code in empty repo and expect nothing to return")
    void findConfirmationCodeByCodeValueInEmptyRepo_andExpectNothingAsResult() {
        // given
        InMemoryConfirmationCodeRepository repo = ConfirmationCodeRepositoryTestingFactory.inMemory();
        String codeValue = "odeyalo";
        // When
        Optional<ConfirmationCode> result = repo.findConfirmationCodeByCodeValue(codeValue);
        // Then
        assertThat(result)
                .as("Empty repository must not return not existing code and must return empty Optional")
                .isEmpty();
    }

    @Test
    @DisplayName("Find not existing  confirmation code in non-empty repo and expect nothing to return")
    void findNotExistingConfirmationCodeByCodeValueInNonEmptyRepo_andExpectNothingAsResult() {
        // given
        InMemoryConfirmationCodeRepository repo = ConfirmationCodeRepositoryTestingFactory.inMemoryPredefinedRandom();
        long beforeFindCount = repo.count();
        String codeValue = "odeyalo";
        // When
        Optional<ConfirmationCode> result = repo.findConfirmationCodeByCodeValue(codeValue);
        // Then
        assertThat(result)
                .as("Empty repository must not return not existing code and must return empty Optional")
                .isEmpty();

        assertThat(repo.count())
                .as("The size of the repository must not be changed if 'find*' method was called")
                .isSameAs(beforeFindCount);
    }

    @Test
    @DisplayName("Find existing  confirmation code in non-empty repo and expect confirmation code to be returned")
    void findExistingConfirmationCodeByCodeValueInNonEmptyRepo_andExpectValueCodeAsResult() {
        // given
        String codeValue = "odeyalo";
        ConfirmationCode expectedConfirmationCode = ConfirmationCodeFaker.withBody(codeValue).get();

        Map<Long, ConfirmationCode> codes = Map.of(
                expectedConfirmationCode.getId(), expectedConfirmationCode
        );

        InMemoryConfirmationCodeRepository repo = ConfirmationCodeRepositoryTestingFactory.inMemoryPredefinedRandomMerged(codes);

        long beforeFindCount = repo.count();

        // When
        Optional<ConfirmationCode> result = repo.findConfirmationCodeByCodeValue(codeValue);

        // Then
        assertThat(result)
                .describedAs("The value must be presented and be the same as provided before!")
                .hasValue(expectedConfirmationCode);

        assertThat(repo.count())
                .as("The size of the repository must not be changed if 'find*' method was called")
                .isSameAs(beforeFindCount);
    }

    @Test
    void save() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void deleteByCodeValue() {
    }

    @Test
    void count() {
    }
}
