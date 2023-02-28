package com.odeyalo.sonata.authentication.support.validation;

import com.odeyalo.sonata.authentication.support.validation.step.UserRegistrationInfoValidationStep;
import com.odeyalo.sonata.authentication.testing.factory.UserRegistrationInfoValidationStepRegistryTestingFactory;
import com.odeyalo.sonata.authentication.testing.stubs.AcceptingUserRegistrationInfoValidationStepStub;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contains the test cases for {@link UserRegistrationInfoValidationStepContainer} class.
 */
class UserRegistrationInfoValidationStepContainerTest {
    @Test
    @DisplayName("Get steps for empty DefaultChainUserRegistrationInfoValidator and expect empty non-null List as result")
    public void getStepsForEmpty_andExpectNonNullAndEmptyList() {
        UserRegistrationInfoValidationStepRegistry registry = UserRegistrationInfoValidationStepRegistryTestingFactory.createEmpty();

        List<UserRegistrationInfoValidationStep> steps = registry.getSteps();

        assertNotNull(steps, "getSteps() method should never return null as result!");
        assertEquals(0, steps.size(), "If nothing was added to registry, then registry should be empty!");
    }

    @Test
    @DisplayName("Get steps for non-empty DefaultChainUserRegistrationInfoValidatorTestingFactory and expect steps what were registered")
    public void getSteps() {
        // Given
        UserRegistrationInfoValidationStepRegistry registry = UserRegistrationInfoValidationStepRegistryTestingFactory.create();
        // When
        List<UserRegistrationInfoValidationStep> steps = registry.getSteps();
        // Then
        assertNotNull(steps, "getSteps() method should never return null as result!");
        assertNotEquals(0, steps.size(), "If collection contains elements, then these elements should be returned!");
    }

    @Test
    @DisplayName("Add ValidationStep to the registry and expect value to be registered")
    public void addStepToRegistry_andExpectValueToBeRegistered() {
        // Given
        UserRegistrationInfoValidationStepRegistry registry = UserRegistrationInfoValidationStepRegistryTestingFactory.createEmpty();
        int sizeBefore = registry.getSteps().size();

        // When
        AcceptingUserRegistrationInfoValidationStepStub stub = new AcceptingUserRegistrationInfoValidationStepStub();

        registry.add(stub);

        List<UserRegistrationInfoValidationStep> steps = registry.getSteps();

        int sizeAfter = steps.size();
        // Then
        assertNotEquals(sizeBefore, sizeAfter, "If the value was added to registry, then size should be incremented!");
        assertEquals(steps.get(0), stub, "If value was registered, then value must be returned!");
    }

    @Test
    @DisplayName("Add null to the registry and expect IllegalArgumentException to be thrown")
    public void addNullStepToRegistry_andExpectException() {
        UserRegistrationInfoValidationStepRegistry registry = UserRegistrationInfoValidationStepRegistryTestingFactory.createEmpty();

        assertThrows(IllegalArgumentException.class, () -> registry.add(null), "Method should throw IllegalArgumentException when argument is null");

    }

    @Test
    @DisplayName("Add value to registry at specific index and expect success")
    public void addValueAtSpecificIndex_andExceptSuccess() {
        // given
        UserRegistrationInfoValidationStepRegistry registry = UserRegistrationInfoValidationStepRegistryTestingFactory.create();
        AcceptingUserRegistrationInfoValidationStepStub stub = new AcceptingUserRegistrationInfoValidationStepStub();
        int sizeBefore = registry.getSteps().size();
        // when
        registry.add(0, stub);

        List<UserRegistrationInfoValidationStep> steps = registry.getSteps();

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
        UserRegistrationInfoValidationStepRegistry registry = UserRegistrationInfoValidationStepRegistryTestingFactory.create();

        AcceptingUserRegistrationInfoValidationStepStub stub = new AcceptingUserRegistrationInfoValidationStepStub();
        int sizeBefore = registry.getSteps().size();
        // when
        registry.add(14, stub);

        List<UserRegistrationInfoValidationStep> steps = registry.getSteps();

        int sizeAfter = steps.size();

        // then
        assertEquals(sizeBefore + 1, sizeAfter, "If the value was added to registry, then size should be incremented!");
        assertEquals(steps.get(sizeAfter - 1), stub, "If the value was added at specific index, then this index should contain the element");
    }

    @Test
    @DisplayName("Add null value to registry at specific index and expect IllegalArgumentException to be thrown")
    public void addNullValueAtSpecificIndex_andExceptException() {
        // given
        UserRegistrationInfoValidationStepRegistry registry = UserRegistrationInfoValidationStepRegistryTestingFactory.create();

        assertThrows(IllegalArgumentException.class, () -> registry.add(1, null));
    }

    @Test
    @DisplayName("Remove value in empty registry and expect nothing")
    public void removeNonExistingValue_andExpectNothingToHappen() {

        UserRegistrationInfoValidationStepRegistry registry = UserRegistrationInfoValidationStepRegistryTestingFactory.createEmpty();
        UserRegistrationInfoValidationStep stub = (info) -> ValidationResult.success();

        List<UserRegistrationInfoValidationStep> before = registry.getSteps();

        registry.remove(stub);
        List<UserRegistrationInfoValidationStep> after = registry.getSteps();

        assertEquals(before, after, "The lists should be equal if nothing was removed!");
    }

    @Test
    @DisplayName("Remove existing value and expect value deletion")
    public void removeExistingValue_andExpectValueDeletion() {
        //Given
        UserRegistrationInfoValidationStepRegistry registry = UserRegistrationInfoValidationStepRegistryTestingFactory.create();
        UserRegistrationInfoValidationStep toDelete = registry.getSteps().get(0);
        List<UserRegistrationInfoValidationStep> before = registry.getSteps();

        //When
        registry.remove(toDelete);

        //Then
        List<UserRegistrationInfoValidationStep> after = registry.getSteps();
        assertNotEquals(before.size(), after.size(), "After value was deleted, size should be decremented");
        assertFalse(after.contains(toDelete));
    }

    @Test
    @DisplayName("Test size for the non-empty UserRegistrationInfoValidationStepRegistry and expect size to be greater than 0")
    public void assertSizeIsGreaterThanZeroForNonEmptyRegistry() {
        UserRegistrationInfoValidationStepRegistry registry = UserRegistrationInfoValidationStepRegistryTestingFactory.create();
        assertFalse(registry.size() <= 0, "The size cannot be less or equal 0 if elements are presented in the registry");
    }

    @Test
    @DisplayName("Test size for the empty UserRegistrationInfoValidationStepRegistry and expect size to be 0")
    public void assertSizeIsEqualZeroForEmptyRegistry() {
        UserRegistrationInfoValidationStepRegistry registry = UserRegistrationInfoValidationStepRegistryTestingFactory.createEmpty();
        assertEquals(0, registry.size(), "if the registry is empty, then 0 should be returned as the registry size");
    }

    @Test
    @DisplayName("Test size for empty UserRegistrationInfoValidationStepRegistry after element was added")
    public void assertSizeIncrementedAfterElementWasAdded() {
        UserRegistrationInfoValidationStepRegistry registry = UserRegistrationInfoValidationStepRegistryTestingFactory.createEmpty();
        int sizeBefore = registry.size();

        registry.add(new AcceptingUserRegistrationInfoValidationStepStub());

        int sizeAfter = registry.size();

        assertEquals(sizeBefore + 1, sizeAfter, "The size should be incremented after the element was added to registry");
    }
}
