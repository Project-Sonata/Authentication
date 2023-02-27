package com.odeyalo.sonata.authentication.support.validation;

import com.odeyalo.sonata.authentication.common.ErrorDetails;
import com.odeyalo.sonata.authentication.dto.request.UserRegistrationInfo;
import com.odeyalo.sonata.authentication.support.validation.step.ValidationStep;
import com.odeyalo.sonata.authentication.testing.factory.DefaultChainUserRegistrationInfoValidatorTestingFactory;
import com.odeyalo.sonata.authentication.testing.stubs.AcceptingUserRegistrationInfoValidationStepStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link DefaultChainUserRegistrationInfoValidator}
 */
class DefaultChainUserRegistrationInfoValidatorTest {
    public static final String ALREADY_TAKEN_EMAIL_VALUE = "alreadytaken@gmail.com";
    public static final String INVALID_EMAIL = "invalidemail";
    public static final String INVALID_PASSWORD_VALUE = "odeyalo";
    private final DefaultChainUserRegistrationInfoValidator validator = DefaultChainUserRegistrationInfoValidatorTestingFactory.create();

    @Nested
    public class ValidateUserRegistrationInfoValuesTest {
        @Test
        @DisplayName("Validate the correct info and expect success")
        void validateCorrectInfo_andExpectSuccess() {
            UserRegistrationInfo info = getValidUserRegistrationInfo();
            ValidationResult validationResult = validator.validateInfo(info);
            assertEquals(validationResult, ValidationResult.success());
        }

        @Test
        @DisplayName("Validate invalid email and expect INVALID_EMAIL error")
        void validateInvalidEmail_andExpectInvalidEmailError() {
            UserRegistrationInfo info = getValidUserRegistrationInfo();
            info.setEmail(INVALID_EMAIL);
            ValidationResult validationResult = validator.validateInfo(info);

            assertFalse(validationResult.isSuccess());
            assertEquals(ErrorDetails.INVALID_EMAIL, validationResult.getErrorDetails());
        }


        @Test
        @DisplayName("Validate already taken email and expect EMAIL_ALREADY_TAKEN error")
        void validateUsedEmail_andExpectEmailAlreadyTakenError() {
            UserRegistrationInfo info = getValidUserRegistrationInfo();
            info.setEmail(ALREADY_TAKEN_EMAIL_VALUE);
            ValidationResult validationResult = validator.validateInfo(info);

            assertFalse(validationResult.isSuccess());
            assertEquals(ErrorDetails.EMAIL_ALREADY_TAKEN, validationResult.getErrorDetails());
        }

        @Test
        @DisplayName("Validate invalid password and expect INVALID_PASSWORD error")
        void validateInvalidPassword_andExpectInvalidPasswordError() {
            UserRegistrationInfo info = getValidUserRegistrationInfo();
            info.setPassword(INVALID_PASSWORD_VALUE);
            ValidationResult validationResult = validator.validateInfo(info);

            assertFalse(validationResult.isSuccess());
            assertEquals(ErrorDetails.INVALID_PASSWORD, validationResult.getErrorDetails());
        }

        private UserRegistrationInfo getValidUserRegistrationInfo() {
            String email = "odeyalo@gmail.com";
            String password = "mysupercoolpassword123";
            LocalDate birthdate = LocalDate.of(2002, 11, 23);
            String gender = "MALE";
            boolean notificationEnabled = true;

            return UserRegistrationInfo.builder()
                    .email(email)
                    .password(password)
                    .birthdate(birthdate)
                    .gender(gender)
                    .notificationEnabled(notificationEnabled)
                    .build();
        }
    }

    @Nested
    public class UserRegistrationInfoValidationStepRegistryTest {

        @Test
        @DisplayName("Get steps for empty DefaultChainUserRegistrationInfoValidator and expect empty non-null List as result")
        public void getStepsForEmpty_andExpectNonNullAndEmptyList() {
            UserRegistrationInfoValidationStepRegistry registry = DefaultChainUserRegistrationInfoValidatorTestingFactory.createEmpty();

            List<ValidationStep<UserRegistrationInfo>> steps = registry.getSteps();

            assertNotNull(steps, "getSteps() method should never return null as result!");
            assertEquals(0, steps.size(), "If nothing was added to registry, then registry should be empty!");
        }

        @Test
        @DisplayName("Get steps for non-empty DefaultChainUserRegistrationInfoValidatorTestingFactory and expect steps what were registered")
        public void getSteps() {
            // Given
            UserRegistrationInfoValidationStepRegistry registry = DefaultChainUserRegistrationInfoValidatorTestingFactory.create();
            // When
            List<ValidationStep<UserRegistrationInfo>> steps = registry.getSteps();
            // Then
            assertNotNull(steps, "getSteps() method should never return null as result!");
            assertNotEquals(0, steps.size(), "If collection contains elements, then these elements should be returned!");
        }

        @Test
        @DisplayName("Add ValidationStep to the registry and expect value to be registered")
        public void addStepToRegistry_andExpectValueToBeRegistered() {
            // Given
            UserRegistrationInfoValidationStepRegistry registry = DefaultChainUserRegistrationInfoValidatorTestingFactory.createEmpty();
            int sizeBefore = registry.getSteps().size();

            // When
            AcceptingUserRegistrationInfoValidationStepStub stub = new AcceptingUserRegistrationInfoValidationStepStub();

            registry.add(stub);

            List<ValidationStep<UserRegistrationInfo>> steps = registry.getSteps();

            int sizeAfter = steps.size();
            // Then
            assertNotEquals(sizeBefore, sizeAfter, "If the value was added to registry, then size should be incremented!");
            assertEquals(steps.get(0), stub, "If value was registered, then value must be returned!");
        }

        @Test
        @DisplayName("Add null to the registry and expect IllegalArgumentException to be thrown")
        public void addNullStepToRegistry_andExpectException() {
            UserRegistrationInfoValidationStepRegistry registry = DefaultChainUserRegistrationInfoValidatorTestingFactory.createEmpty();

            assertThrows(IllegalArgumentException.class, () -> registry.add(null), "Method should throw IllegalArgumentException when argument is null");

        }

        @Test
        @DisplayName("Add value to registry at specific index and expect success")
        public void addValueAtSpecificIndex_andExceptSuccess() {
            // given
            UserRegistrationInfoValidationStepRegistry registry = DefaultChainUserRegistrationInfoValidatorTestingFactory.create();
            AcceptingUserRegistrationInfoValidationStepStub stub = new AcceptingUserRegistrationInfoValidationStepStub();
            int sizeBefore = registry.getSteps().size();
            // when
            registry.add(0, stub);

            List<ValidationStep<UserRegistrationInfo>> steps = registry.getSteps();

            int sizeAfter = steps.size();

            // then
            assertNotEquals(sizeBefore, sizeAfter, "If the value was added to registry, then size should be incremented!");
            assertEquals(sizeBefore + 1, sizeAfter, "If the value was added to registry, then size should be incremented!");
            assertEquals(steps.get(0), stub, "If the value was added at specific index, then this index should contain the element");
        }

        @Test
        @DisplayName("Add value in the out of size and expect element to be added in the end")
        public void addValueInTheEnd_andExceptElementInTheEnd() {
            // given
            UserRegistrationInfoValidationStepRegistry registry = DefaultChainUserRegistrationInfoValidatorTestingFactory.create();

            AcceptingUserRegistrationInfoValidationStepStub stub = new AcceptingUserRegistrationInfoValidationStepStub();
            int sizeBefore = registry.getSteps().size();
            // when
            registry.add(14, stub);

            List<ValidationStep<UserRegistrationInfo>> steps = registry.getSteps();

            int sizeAfter = steps.size();

            // then
            assertNotEquals(sizeBefore, sizeAfter, "If the value was added to registry, then size should be incremented!");
            assertEquals(sizeBefore + 1, sizeAfter, "If the value was added to registry, then size should be incremented!");
            assertEquals(steps.get(sizeAfter), stub, "If the value was added at specific index, then this index should contain the element");
        }

        @Test
        @DisplayName("Add null value to registry at specific index and expect IllegalArgumentException to be thrown")
        public void addNullValueAtSpecificIndex_andExceptException() {
            // given
            UserRegistrationInfoValidationStepRegistry registry = DefaultChainUserRegistrationInfoValidatorTestingFactory.create();

            assertThrows(IllegalArgumentException.class, () -> registry.add(1, null));
        }

        @Test
        @DisplayName("Remove value in empty registry and expect nothing")
        public void removeNonExistingValue_andExpectNothingToHappen() {

            UserRegistrationInfoValidationStepRegistry registry = DefaultChainUserRegistrationInfoValidatorTestingFactory.createEmpty();
            ValidationStep<UserRegistrationInfo> stub = (info) -> ValidationResult.success();

            List<ValidationStep<UserRegistrationInfo>> before = registry.getSteps();

            registry.remove(stub);
            List<ValidationStep<UserRegistrationInfo>> after = registry.getSteps();

            assertEquals(before, after, "The lists should be equal if nothing was removed!");
        }

        @Test
        @DisplayName("Remove existing value and expect value deletion")
        public void removeExistingValue_andExpectValueDeletion() {
            //Given
            UserRegistrationInfoValidationStepRegistry registry = DefaultChainUserRegistrationInfoValidatorTestingFactory.create();
            ValidationStep<UserRegistrationInfo> toDelete = registry.getSteps().get(0);
            List<ValidationStep<UserRegistrationInfo>> before = registry.getSteps();

            //When
            registry.remove(toDelete);

            //Then
            List<ValidationStep<UserRegistrationInfo>> after = registry.getSteps();
            assertNotEquals(before.size(), after.size(), "After value was deleted, size should be decremented");
            assertFalse(after.contains(toDelete));
        }

        @Test
        @DisplayName("Test size for the non-empty UserRegistrationInfoValidationStepRegistry and expect size to be greater than 0")
        public void assertSizeIsGreaterThanZeroForNonEmptyRegistry() {
            UserRegistrationInfoValidationStepRegistry registry = DefaultChainUserRegistrationInfoValidatorTestingFactory.create();
            assertFalse(registry.size() <= 0, "The size cannot be less or equal 0 if elements are presented in the registry");
        }

        @Test
        @DisplayName("Test size for the empty UserRegistrationInfoValidationStepRegistry and expect size to be 0")
        public void assertSizeIsEqualZeroForEmptyRegistry() {
            UserRegistrationInfoValidationStepRegistry registry = DefaultChainUserRegistrationInfoValidatorTestingFactory.createEmpty();
            assertEquals(0, registry.size(), "if the registry is empty, then 0 should be returned as the registry size");
        }

        @Test
        @DisplayName("Test size for empty UserRegistrationInfoValidationStepRegistry after element was added")
        public void assertSizeIncrementedAfterElementWasAdded() {
            UserRegistrationInfoValidationStepRegistry registry = DefaultChainUserRegistrationInfoValidatorTestingFactory.createEmpty();
            int sizeBefore = registry.size();

            registry.add(new AcceptingUserRegistrationInfoValidationStepStub());

            int sizeAfter = registry.size();

            assertEquals(sizeBefore + 1,sizeAfter, "The size should be incremented after the element was added to registry");
        }
    }
}
