package com.odeyalo.sonata.authentication.support.validation;

import com.odeyalo.sonata.authentication.support.validation.step.ValidationStep;

import java.util.List;

/**
 * Registry interface used to registry {@link ValidationStep} in some container
 *
 * @param <T> - type of the ValidationStep to registry
 */
public interface ValidationStepRegistry<T extends ValidationStep<?>> {

    /**
     * Return all registered {@link ValidationStep}
     * Note: The implementation should return NEW List with steps to make it immutable.
     * @return - all registered steps in new list
     */
    List<T> getSteps();

    /**
     * Add the given step to registry
     * @param step - step to add into the registry
     */
    void add(T step);

    /**
     * Add the given step to registry at specific index
     * @param index - index to insert the step
     * @param step - step to add
     */
    void add(int index, T step);

    /**
     * Remove the ValidationStep from the registry
     * @param step - step to remove
     */
    void remove(T step);

    /**
     * Return the size of the registry
     * @return - size of the registry
     */
    int size();

    /**
     * Remove all registered {@link ValidationStep} from registry
     */
    void clear();
}
