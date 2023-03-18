package com.odeyalo.sonata.authentication.service.confirmation.support;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;

/**
 * Interface that manages lifecycle of the confirmation code
 */
public interface ConfirmationCodeLifecycleHandler {
    /**
     * Provide the info about current confirmation code lifecycle stage
     * <p>
     *     <strong>NOTE: </strong> The method SHOULD NOT return the code lifecycle stage by using the {@link ConfirmationCode#getLifecycleStage()} method
     *     and SHOULD do it by returning the current lifecycle saved by the implementation class
     * </p>
     * @param code - confirmation code entity
     * @return - lifecycle stage of the provided confirmation code
     */
    ConfirmationCode.LifecycleStage getLifecycleStage(ConfirmationCode code);

    /**
     * Same as {@link #getLifecycleStage(ConfirmationCode)} but do it by code value.
     * @param codeValue - confirmation code value
     * @return - lifecycle stage of the provided confirmation code
     */
    ConfirmationCode.LifecycleStage getLifecycleStage(String codeValue);

    /**
     * Change the confirmation code lifecycle stage to provided
     * @param code - confirmation code to change the lifecycle stage
     * @param stage - required stage to set
     * @return - updated {@link ConfirmationCode}
     */
    ConfirmationCode changeLifecycleStage(ConfirmationCode code, ConfirmationCode.LifecycleStage stage);

    /**
     * Same as {@link #changeLifecycleStage(ConfirmationCode, ConfirmationCode.LifecycleStage)} but do it with confirmation code value.
     * @param codeValue - confirmation code value to change the lifecycle stage
     * @param stage - required stage to set
     * @return - updated {@link ConfirmationCode}
     */
    ConfirmationCode changeLifecycleStage(String codeValue,  ConfirmationCode.LifecycleStage stage);
}
