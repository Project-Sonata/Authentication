package com.odeyalo.sonata.authentication.testing.factory;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.repository.ConfirmationCodeRepository;
import com.odeyalo.sonata.authentication.repository.InMemoryConfirmationCodeRepository;
import com.odeyalo.sonata.authentication.testing.faker.ConfirmationCodeFaker;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains the methods to create a new {@link ConfirmationCodeRepository} for tests
 */
public class ConfirmationCodeRepositoryTestingFactory {

    public static InMemoryConfirmationCodeRepository inMemory() {
        return new InMemoryConfirmationCodeRepository();
    }

    public static InMemoryConfirmationCodeRepository inMemoryPredefined(Map<Long, ConfirmationCode> codes) {
        return new InMemoryConfirmationCodeRepository(codes);
    }

    public static InMemoryConfirmationCodeRepository inMemoryPredefinedRandom() {
        ConfirmationCode code1 = ConfirmationCodeFaker.alphanumeric().get();
        ConfirmationCode code2 = ConfirmationCodeFaker.numeric().get();
        ConfirmationCode code3 = ConfirmationCodeFaker.onlyLetters().get();

        Map<Long, ConfirmationCode> predefined = Map.of(
                code1.getId(), code1,
                code2.getId(), code2,
                code3.getId(), code3
        );

        return inMemoryPredefined(predefined);
    }

    /**
     * Create a new InMemoryConfirmationCodeRepository with random confirmation codes and with users provided in params
     * @param codes - codes that will be merged to random generated confirmation codes
     * @return - InMemoryConfirmationCodeRepository with random generated codes and codes provided in params
     */
    public static InMemoryConfirmationCodeRepository inMemoryPredefinedRandomMerged(Map<Long, ConfirmationCode> codes) {
        ConfirmationCode code1 = ConfirmationCodeFaker.alphanumeric().get();
        ConfirmationCode code2 = ConfirmationCodeFaker.numeric().get();
        ConfirmationCode code3 = ConfirmationCodeFaker.onlyLetters().get();

        Map<Long, ConfirmationCode> predefined = Map.of(
                code1.getId(), code1,
                code2.getId(), code2,
                code3.getId(), code3
        );
        // Copy the provided and generated codes to result map to avoid UnsupportedOperationException if the 'codes' map is immutable
        Map<Long, ConfirmationCode> result = new HashMap<>(codes);

        result.putAll(predefined);

        return inMemoryPredefined(result);
    }

    public static ConfirmationCodeRepository create() {
        return inMemory();
    }
}
