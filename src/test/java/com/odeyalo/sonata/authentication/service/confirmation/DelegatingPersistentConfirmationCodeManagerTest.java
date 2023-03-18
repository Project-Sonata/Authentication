package com.odeyalo.sonata.authentication.service.confirmation;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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
    @DisplayName("Verify DENIED code BUT not expired code and expect ConfirmationCodeCheckResult.ALREADY_ACTIVATED")
    void verifyActivatedButNotExpiredCode_andExpectAlreadyActivatedStatus() {
        // given
        ConfirmationCode activatedCode = ConfirmationCodeFaker.numeric().atSpecificLifecycle(ConfirmationCode.LifecycleStage.DENIED).get();
        DelegatingPersistentConfirmationCodeManager persistentManager = ConfirmationCodeManagerTestingFactory.createPersistentManagerBuilder()
                .withPredefinedCodes(activatedCode)
                .build();
        // when
        ConfirmationCodeCheckResult result = persistentManager.verifyCodeAndActive(activatedCode.getCode());
        // then
        assertThat(result)
                .as("If code is activated but not expired, then ConfirmationCodeCheckResult.ALREADY_ACTIVATED must be returned")
                .isEqualTo(ConfirmationCodeCheckResult.ALREADY_ACTIVATED);
    }

    @Test
    @DisplayName("Delete the not existing code by value and expect nothing to be deleted")
    void deleteNotExistingCodeValue_andExpectNothingToBeDeleted() {
        // given
        String notExistingCodeValue = "I just want to hide my face";
        ConfirmationCode code1 = ConfirmationCodeFaker.numeric().get();
        ConfirmationCode code2 = ConfirmationCodeFaker.numeric().get();
        DelegatingPersistentConfirmationCodeManager persistentManager = ConfirmationCodeManagerTestingFactory.createPersistentManagerBuilder()
                .withPredefinedCodes(code1, code2)
                .build();
        // when
        persistentManager.deleteCode(notExistingCodeValue);
        // then
        Optional<ConfirmationCode> foundCode1 = persistentManager.findByCodeValue(code1.getCode());
        Optional<ConfirmationCode> foundCode2 = persistentManager.findByCodeValue(code2.getCode());

        ConfirmationCodeAssert.forOptional(foundCode1)
                .as("If code does not exist, then nothing must be deleted")
                .isEqualTo(code1);
        ConfirmationCodeAssert.forOptional(foundCode2)
                .as("If code does not exist, then nothing must be deleted")
                .isEqualTo(code2);
    }

    @Test
    @DisplayName("Delete existing code by value and expect code to be deleted")
    void deleteExistingCodeValue_andExpectCodeToBeDeleted() {
        // given
        ConfirmationCode code1 = ConfirmationCodeFaker.numeric().get();
        ConfirmationCode code2 = ConfirmationCodeFaker.numeric().get();

        DelegatingPersistentConfirmationCodeManager persistentConfirmationCodeManager = ConfirmationCodeManagerTestingFactory.createPersistentManagerBuilder()
                .withPredefinedCodes(code1, code2)
                .build();
        // when
        persistentConfirmationCodeManager.deleteCode(code1.getCode());

        // then
        Optional<ConfirmationCode> foundCode1 = persistentConfirmationCodeManager.findByCodeValue(code1.getCode());
        Optional<ConfirmationCode> foundCode2 = persistentConfirmationCodeManager.findByCodeValue(code2.getCode());

        assertThat(foundCode1)
                .as("The code must be deleted if code value exists in manager!")
                .isEmpty();

        ConfirmationCodeAssert.forOptional(foundCode2)
                .as("Only confirmation code that has provided code value must be deleted")
                .isEqualTo(code2);
    }

    @Test
    @DisplayName("Delete by code entity and expect code to be deleted")
    void deleteByEntity_andExpectToBeDeleted() {
        // given
        ConfirmationCode code1 = ConfirmationCodeFaker.numeric().overrideId(null).get();
        ConfirmationCode code2 = ConfirmationCodeFaker.numeric().get();

        DelegatingPersistentConfirmationCodeManager persistentConfirmationCodeManager = ConfirmationCodeManagerTestingFactory.createPersistentManagerBuilder()
                .withPredefinedCodes(code1, code2)
                .build();
        // when
        persistentConfirmationCodeManager.deleteCode(code1);

        // then
        Optional<ConfirmationCode> foundCode1 = persistentConfirmationCodeManager.findByCodeValue(code1.getCode());
        Optional<ConfirmationCode> foundCode2 = persistentConfirmationCodeManager.findByCodeValue(code2.getCode());

        assertThat(foundCode1)
                .as("The code must be deleted if code exists in manager!")
                .isEmpty();

        ConfirmationCodeAssert.forOptional(foundCode2)
                .as("Only confirmation code that has provided code must be deleted")
                .isEqualTo(code2);
    }

    @Test
    @DisplayName("Delete the non-existing code by entity value and expect nothing to be deleted")
    void deleteNotExistingCodeEntity_andExpectNothingToBeDeleted() {
        // given
        String notExistingCodeValue = "I just want to hide my face";
        ConfirmationCode code1 = ConfirmationCodeFaker.numeric().get();
        ConfirmationCode code2 = ConfirmationCodeFaker.numeric().get();
        ConfirmationCode notExistingCode = ConfirmationCodeFaker.withBody(notExistingCodeValue).get();
        DelegatingPersistentConfirmationCodeManager persistentManager = ConfirmationCodeManagerTestingFactory.createPersistentManagerBuilder()
                .withPredefinedCodes(code1, code2)
                .build();
        // when
        persistentManager.deleteCode(notExistingCode);
        // then
        Optional<ConfirmationCode> foundCode1 = persistentManager.findByCodeValue(code1.getCode());
        Optional<ConfirmationCode> foundCode2 = persistentManager.findByCodeValue(code2.getCode());

        ConfirmationCodeAssert.forOptional(foundCode1)
                .as("If code does not exist, then nothing must be deleted")
                .isEqualTo(code1);
        ConfirmationCodeAssert.forOptional(foundCode2)
                .as("If code does not exist, then nothing must be deleted")
                .isEqualTo(code2);
    }

    @Test
    @DisplayName("Delete by id and expect code to be deleted")
    void deleteById_andExpectCodeToBeDeleted() {
        // given
        ConfirmationCode code1 = ConfirmationCodeFaker.numeric().get();
        ConfirmationCode code2 = ConfirmationCodeFaker.numeric().get();
        DelegatingPersistentConfirmationCodeManager persistentManager = ConfirmationCodeManagerTestingFactory.createPersistentManagerBuilder()
                .withPredefinedCodes(code1, code2)
                .build();

        // when
        ConfirmationCode toDelete = ConfirmationCodeFaker.numeric().overrideId(code1.getId()).get();
        persistentManager.deleteCode(toDelete);

        // then
        Optional<ConfirmationCode> foundCode1 = persistentManager.findByCodeValue(code1.getCode());
        Optional<ConfirmationCode> foundCode2 = persistentManager.findByCodeValue(code2.getCode());

        assertThat(foundCode1)
                .as("The code must be deleted if code ID exists in manager!")
                .isEmpty();

        ConfirmationCodeAssert.forOptional(foundCode2)
                .as("Only confirmation code that has provided code must be deleted")
                .isEqualTo(code2);
    }

    @Test
    @DisplayName("Get lifecycle stage of the code and expect stage to be CREATED")
    void getLifecycleStageForNewCode_andExpectStageToBeCreated() {
        // given
        DelegatingPersistentConfirmationCodeManager persistentConfirmationCodeManager = ConfirmationCodeManagerTestingFactory.createPersistentManager();
        // when
        ConfirmationCode newCode = persistentConfirmationCodeManager.generateCode();
        ConfirmationCode.LifecycleStage actualLifecycleStage = persistentConfirmationCodeManager.getLifecycleStage(newCode);
        // then
        assertThat(actualLifecycleStage)
                .as("If code was created just now, then LifecycleStage should be CREATED")
                .isEqualTo(ConfirmationCode.LifecycleStage.CREATED);
    }

    @Test
    @DisplayName("Get lifecycle stage for activated code and expect LifecycleStage.ACTIVATED")
    void getStageForActivatedCode_andExpectStageToBeActivated() {
        // given
        DelegatingPersistentConfirmationCodeManager persistentConfirmationCodeManager = ConfirmationCodeManagerTestingFactory.createPersistentManager();
        // when
        ConfirmationCode newCode = persistentConfirmationCodeManager.generateCode();
        // Activate the existing code to change lifecycle stage and check if the stage has been changed
        persistentConfirmationCodeManager.verifyCodeAndActive(newCode.getCode());
        ConfirmationCode.LifecycleStage actualLifecycleStage = persistentConfirmationCodeManager.getLifecycleStage(newCode);
        // then
        assertThat(actualLifecycleStage)
                .as("If the code was activated, then lifecycle stage must be changed to ACTIVATED")
                .isEqualTo(ConfirmationCode.LifecycleStage.ACTIVATED);
    }

    @ParameterizedTest
    @EnumSource(value = ConfirmationCode.LifecycleStage.class)
    @DisplayName("Get lifecycle stage for code and expect correct stage to be returned")
    void getLifeCycleStageForCode_andExpectCorrectStage(ConfirmationCode.LifecycleStage expectedLifecycleStage) {
        // given
        ConfirmationCode code = ConfirmationCodeFaker.numeric()
                .atSpecificLifecycle(expectedLifecycleStage)
                .get();

        DelegatingPersistentConfirmationCodeManager persistentConfirmationCodeManager = ConfirmationCodeManagerTestingFactory
                .createPersistentManagerBuilder()
                .withPredefinedCodes(code)
                .build();
        // when
        ConfirmationCode.LifecycleStage lifecycleStage = persistentConfirmationCodeManager.getLifecycleStage(code);
        // then
        assertThat(lifecycleStage)
                .as("If code exists in manager, then current code lifecycle stage must be returned!")
                .isEqualTo(expectedLifecycleStage);
    }

    @Test
    @DisplayName("Retrieve lifecycle stage of the non-existing code and expect a null value to be returned")
    void getStageOfNotExistingCode_andExpectNull() {
        // given
        ConfirmationCode confirmationCode = ConfirmationCodeFaker.numeric().get();
        DelegatingPersistentConfirmationCodeManager persistentConfirmationCodeManager = ConfirmationCodeManagerTestingFactory.createPersistentManager();
        // when
        ConfirmationCode.LifecycleStage actualStage = persistentConfirmationCodeManager.getLifecycleStage(confirmationCode);
        // then
        assertThat(actualStage)
                .describedAs("If code does not exist, then null must be returned")
                .isEqualTo(null);
    }

    @Test
    @DisplayName("Get lifecycle stage of the code by code value and expect stage to be CREATED")
    void getLifecycleStageByCodeValue_andExpectStageToBeCreated() {
        // given
        DelegatingPersistentConfirmationCodeManager persistentConfirmationCodeManager = ConfirmationCodeManagerTestingFactory.createPersistentManager();
        // when
        ConfirmationCode newCode = persistentConfirmationCodeManager.generateCode();
        ConfirmationCode.LifecycleStage actualLifecycleStage = persistentConfirmationCodeManager.getLifecycleStage(newCode.getCode());
        // then
        assertThat(actualLifecycleStage)
                .as("If code was created just now, then LifecycleStage should be CREATED")
                .isEqualTo(ConfirmationCode.LifecycleStage.CREATED);
    }


    @Test
    @DisplayName("Get lifecycle stage for activated code by code value and expect LifecycleStage.ACTIVATED")
    void getStageForActivatedCodeByCodeValue_andExpectStageToBeActivated() {
        // given
        DelegatingPersistentConfirmationCodeManager persistentConfirmationCodeManager = ConfirmationCodeManagerTestingFactory.createPersistentManager();
        // when
        ConfirmationCode newCode = persistentConfirmationCodeManager.generateCode();
        // Activate the existing code to change lifecycle stage and check if the stage has been changed
        persistentConfirmationCodeManager.verifyCodeAndActive(newCode.getCode());
        ConfirmationCode.LifecycleStage actualLifecycleStage = persistentConfirmationCodeManager.getLifecycleStage(newCode.getCode());
        // then
        assertThat(actualLifecycleStage)
                .as("If the code was activated, then lifecycle stage must be changed to ACTIVATED")
                .isEqualTo(ConfirmationCode.LifecycleStage.ACTIVATED);
    }

    @ParameterizedTest
    @EnumSource(value = ConfirmationCode.LifecycleStage.class)
    @DisplayName("Get lifecycle stage for code and expect correct stage to be returned")
    void getLifeCycleStageForCodeByCodeValue_andExpectCorrectStage(ConfirmationCode.LifecycleStage expectedLifecycleStage) {
        // given
        ConfirmationCode code = ConfirmationCodeFaker.numeric()
                .atSpecificLifecycle(expectedLifecycleStage)
                .get();

        DelegatingPersistentConfirmationCodeManager persistentConfirmationCodeManager = ConfirmationCodeManagerTestingFactory
                .createPersistentManagerBuilder()
                .withPredefinedCodes(code)
                .build();
        // when
        ConfirmationCode.LifecycleStage lifecycleStage = persistentConfirmationCodeManager.getLifecycleStage(code.getCode());
        // then
        assertThat(lifecycleStage)
                .as("If code exists in manager, then current code lifecycle stage must be returned!")
                .isEqualTo(expectedLifecycleStage);
    }


    @Test
    @DisplayName("Retrieve lifecycle stage of the non-existing code and expect a null value to be returned")
    void getStageOfNotExistingCodeByCodeValue_andExpectNull() {
        // given
        String notExistingCodeValue = "Once in a while when i wake up I find myself crying";
        DelegatingPersistentConfirmationCodeManager persistentConfirmationCodeManager = ConfirmationCodeManagerTestingFactory.createPersistentManager();
        // when
        ConfirmationCode.LifecycleStage actualStage = persistentConfirmationCodeManager.getLifecycleStage(notExistingCodeValue);
        // then
        assertThat(actualStage)
                .describedAs("If code does not exist, then null must be returned")
                .isEqualTo(null);
    }

    @ParameterizedTest
    @EnumSource(value = ConfirmationCode.LifecycleStage.class)
    @DisplayName("Change the lifecycle of the code and expect lifecycle stage to be changed")
    void changeLifecycleStage_andExpectStageToBeChanged(ConfirmationCode.LifecycleStage stage) {
        // given
        ConfirmationCode confirmationCode = ConfirmationCodeFaker.numeric().get();

        DelegatingPersistentConfirmationCodeManager persistentConfirmationCodeManager = ConfirmationCodeManagerTestingFactory
                .createPersistentManagerBuilder()
                .withPredefinedCodes(confirmationCode)
                .build();
        // when
        ConfirmationCode updatedConfirmationCode = persistentConfirmationCodeManager.changeLifecycleStage(confirmationCode, stage);
        // then

        assertThat(updatedConfirmationCode)
                .describedAs("Updated confirmation code must be not null!")
                .isNotNull();

        ConfirmationCodeAssert.forCode(updatedConfirmationCode)
                .equals(confirmationCode)
                .but()
                .lifecycleStageMustEqualsTo(stage)
                .check();
    }

    @Test
    @DisplayName("Change the lifecycle of the non-existing code and expect null as result")
    void changeLifecycleOfNotExistingCode_andExpectNothingToChange() {
        // given

        ConfirmationCode notExistingCode = ConfirmationCodeFaker.withBody("DeadPixel").get();
        ConfirmationCode confirmationCode = ConfirmationCodeFaker.numeric().get();
        DelegatingPersistentConfirmationCodeManager persistentConfirmationCodeManager = ConfirmationCodeManagerTestingFactory
                .createPersistentManagerBuilder()
                .withPredefinedCodes(confirmationCode)
                .build();
        // when
        ConfirmationCode updatedCode = persistentConfirmationCodeManager.changeLifecycleStage(notExistingCode, ConfirmationCode.LifecycleStage.DENIED);
        // then
        assertThat(updatedCode)
                .as("If the code does not exist in the manager, then null must be returned and nothing must be affected")
                .isNull();

        Optional<ConfirmationCode> optional = persistentConfirmationCodeManager.findByCodeValue(confirmationCode.getCode());
        ConfirmationCodeAssert.forOptional(optional)
                .as("Nothing must be affected if the provided code does not exist in the manager!")
                .isEqualTo(confirmationCode);
    }

    @ParameterizedTest
    @EnumSource(value = ConfirmationCode.LifecycleStage.class)
    @DisplayName("Change the lifecycle of the code by code value and expect lifecycle stage to be changed")
    void changeLifecycleStageByCodeValue_andExpectStageToBeChanged(ConfirmationCode.LifecycleStage stage) {
        // given
        ConfirmationCode confirmationCode = ConfirmationCodeFaker.numeric().get();

        DelegatingPersistentConfirmationCodeManager persistentConfirmationCodeManager = ConfirmationCodeManagerTestingFactory
                .createPersistentManagerBuilder()
                .withPredefinedCodes(confirmationCode)
                .build();
        // when
        ConfirmationCode updatedConfirmationCode = persistentConfirmationCodeManager.changeLifecycleStage(confirmationCode.getCode(), stage);
        // then

        assertThat(updatedConfirmationCode)
                .describedAs("Updated confirmation code must be not null!")
                .isNotNull();

        ConfirmationCodeAssert.forCode(updatedConfirmationCode)
                .equals(confirmationCode)
                .but()
                .lifecycleStageMustEqualsTo(stage)
                .check();
    }

    @Test
    @DisplayName("Change the lifecycle of the non-existing code by code value and expect null as result")
    void changeLifecycleOfNotExistingCodeByCodeValue_andExpectNothingToChange() {
        // given

        ConfirmationCode notExistingCode = ConfirmationCodeFaker.withBody("DeadPixel").get();
        ConfirmationCode confirmationCode = ConfirmationCodeFaker.numeric().get();
        DelegatingPersistentConfirmationCodeManager persistentConfirmationCodeManager = ConfirmationCodeManagerTestingFactory
                .createPersistentManagerBuilder()
                .withPredefinedCodes(confirmationCode)
                .build();
        // when
        ConfirmationCode updatedCode = persistentConfirmationCodeManager.changeLifecycleStage(notExistingCode.getCode(), ConfirmationCode.LifecycleStage.DENIED);
        // then
        assertThat(updatedCode)
                .as("If the code does not exist in the manager, then null must be returned and nothing must be affected")
                .isNull();

        Optional<ConfirmationCode> optional = persistentConfirmationCodeManager.findByCodeValue(confirmationCode.getCode());
        ConfirmationCodeAssert.forOptional(optional)
                .as("Nothing must be affected if the provided code does not exist in the manager!")
                .isEqualTo(confirmationCode);
    }
}
