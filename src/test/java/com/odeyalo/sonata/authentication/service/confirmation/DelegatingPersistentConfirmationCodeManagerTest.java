package com.odeyalo.sonata.authentication.service.confirmation;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import com.odeyalo.sonata.authentication.repository.InMemoryConfirmationCodeRepository;
import com.odeyalo.sonata.authentication.repository.NullConfirmationCodeRepositoryStub;
import com.odeyalo.sonata.authentication.service.confirmation.support.ConfirmationCodeCheckResult;
import com.odeyalo.sonata.authentication.testing.assertations.ConfirmationCodeAssert;
import com.odeyalo.sonata.authentication.testing.factory.ConfirmationCodeManagerTestingFactory;
import com.odeyalo.sonata.authentication.testing.factory.ConfirmationCodeRepositoryTestingFactory;
import com.odeyalo.sonata.authentication.testing.faker.ConfirmationCodeFaker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DelegatingPersistentConfirmationCodeManagerTest {

    @Test
    @DisplayName("Find existing code by code value and expect confirmation code to return")
    void findByExistingCodeValue_andExpectConfirmationCodeToBeReturned() {
        // given
        ConfirmationCode confirmationCode = ConfirmationCodeFaker.numeric().get();
        InMemoryConfirmationCodeRepository repo = ConfirmationCodeRepositoryTestingFactory.inMemoryPredefined(confirmationCode);

        DelegatingPersistentConfirmationCodeManager persistentManager = ConfirmationCodeManagerTestingFactory
                .createPersistentManagerBuilder()
                .overrideConfirmationCodeRepository(repo)
                .build();
        // when
        Optional<ConfirmationCode> code = persistentManager.findByCodeValue(confirmationCode.getCode());
        // then
        assertThat(code)
                .as("The confirmation code must be presented if the code exists in the manager!")
                .isPresent()
                .hasValue(confirmationCode);
    }

    @Test
    @DisplayName("Find not existing code and expect Optional.empty as result")
    void findByNotExistingCodeValue_andExpectEmpty() {
        // given
        String notExistingCode = "odeyalo is sad :(";
        InMemoryConfirmationCodeRepository repo = ConfirmationCodeRepositoryTestingFactory.inMemoryPredefinedRandom();

        DelegatingPersistentConfirmationCodeManager persistentManager = ConfirmationCodeManagerTestingFactory
                .createPersistentManagerBuilder()
                .overrideConfirmationCodeRepository(repo)
                .build();

        // when
        Optional<ConfirmationCode> actual = persistentManager.findByCodeValue(notExistingCode);

        // then
        assertThat(actual)
                .as("If the code does not exist in manager, then findByCodeValue() method must return Optional.empty()")
                .isEmpty();
    }

    @Test
    @DisplayName("Generate confirmation code and expect code to be valid and saved")
    void generateCode_andExpectCodeToBeGeneratedAndSaved() {
        int length = 10;
        int lifetime = 10;
        // given
        DelegatingPersistentConfirmationCodeManager persistentManager = ConfirmationCodeManagerTestingFactory.createPersistentManager();
        // when
        ConfirmationCode generatedCode = persistentManager.generateCode(length, lifetime);
        // then
        ConfirmationCodeAssert.forCode(generatedCode)
                .compareCreatedAndExpirationTime()
                .specificCodeValueLength(length)
                .confirmationCodeLifetime(lifetime)
                .shouldBeNotActivated();

        Optional<ConfirmationCode> savedCode = persistentManager.findByCodeValue(generatedCode.getCode());

        assertThat(savedCode)
                .describedAs("The code must be presented in DelegatingPersistentConfirmationCodeManager impl is using")
                .isPresent()
                .describedAs("Saved and code that was returned must be equal!")
                .hasValue(generatedCode);
    }

    @Test
    @DisplayName("Verify existing and valid code and expect code to be activated and success to be returned")
    void verifyValidCodeAndExpectCodeToBeActivated() {
        // given
        ConfirmationCode confirmationCode = ConfirmationCodeFaker.numeric().get();

        DelegatingPersistentConfirmationCodeManager persistentManager = ConfirmationCodeManagerTestingFactory
                .createPersistentManagerBuilder()
                .withPredefinedCodes(confirmationCode)
                .build();
        // when
        ConfirmationCodeCheckResult result = persistentManager.verifyCodeAndActive(confirmationCode.getCode());
        // then
        assertThat(result.isValid())
                .as("if code is exists in manager, then this code must be activated!")
                .isTrue();
        Optional<ConfirmationCode> activatedCode = persistentManager.findByCodeValue(confirmationCode.getCode());

        assertThat(activatedCode)
                .describedAs("Confirmation code should not be deleted after activation")
                .isPresent();

        ConfirmationCodeAssert.forOptional(activatedCode)
                .lifecycleStage(ConfirmationCode.LifecycleStage.ACTIVATED)
                .shouldBeActivated();
    }

    @Test
    @DisplayName("Verify not existing code and expect code to be not activated and invalid error to be returned")
    void verifyWrongCode_andExpectInvalidCodeResultToBeReturned() {
        // given
        NullConfirmationCodeRepositoryStub stub = new NullConfirmationCodeRepositoryStub();
        String notExistingCode = "Miku is the best<3";

        DelegatingPersistentConfirmationCodeManager persistentManager = ConfirmationCodeManagerTestingFactory.createPersistentManagerBuilder()
                .overrideConfirmationCodeRepository(stub)
                .build();

        //when
        ConfirmationCodeCheckResult result = persistentManager.verifyCodeAndActive(notExistingCode);

        // then
        assertThat(result)
                .describedAs("If the code does not exist, then ConfirmationCodeCheckResult.INVALID_CODE must be returned")
                .isEqualTo(ConfirmationCodeCheckResult.INVALID_CODE);
    }

    @Test
    @DisplayName("Verify expired confirmation code and expect ConfirmationCodeCheckResult.ALREADY_EXPIRED to be returned")
    void verifyExpiredCode_andExpectAlreadyExpiredToBeReturned() {
        // given
        ConfirmationCode expiredCode = ConfirmationCodeFaker.expired().get();
        DelegatingPersistentConfirmationCodeManager persistentManager = ConfirmationCodeManagerTestingFactory.createPersistentManagerBuilder()
                .withPredefinedCodes(expiredCode)
                .build();
        // when
        ConfirmationCodeCheckResult actualResult = persistentManager.verifyCodeAndActive(expiredCode.getCode());
        // then
        assertThat(actualResult)
                .describedAs("If code is expired, then ConfirmationCodeCheckResult.ALREADY_EXPIRED must be returned")
                .isEqualTo(ConfirmationCodeCheckResult.ALREADY_EXPIRED);

        Optional<ConfirmationCode> code = persistentManager.findByCodeValue(expiredCode.getCode());
        assertThat(code)
                .describedAs("Confirmation code should not be deleted if it's expired or denied. " +
                        "It should be saved for at least 30 minutes and only after that it can be deleted")
                .isPresent();

        ConfirmationCodeAssert.forOptional(code)
                .shouldBeNotActivated()
                .lifecycleStage(ConfirmationCode.LifecycleStage.DENIED);
    }

    @Test
    void deleteCode() {
    }

    @Test
    void testDeleteCode() {
    }

    @Test
    void getLifecycleStage() {
    }

    @Test
    void testGetLifecycleStage() {
    }

    @Test
    void changeLifecycleStage() {
    }

    @Test
    void testChangeLifecycleStage() {
    }
}
