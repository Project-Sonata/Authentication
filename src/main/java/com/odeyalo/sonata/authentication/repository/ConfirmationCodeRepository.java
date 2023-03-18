package com.odeyalo.sonata.authentication.repository;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;

import java.util.Optional;

/**
 * Basic repository to working with {@link ConfirmationCode}
 */
public interface ConfirmationCodeRepository {
    /**
     * Find a {@link ConfirmationCode} by id
     * @param id - confirmation code's id
     * @return - ConfirmationCode wrapped in Optional, {@link Optional#empty()} otherwise
     */
    Optional<ConfirmationCode> findById(Long id);

    /**
     * Find the confirmation code by code value
     * @param codeValue - code value associated with ConfirmationCode
     * @return - ConfirmationCode that was found wrapped in Optional, {@link Optional#empty()} otherwise
     */
    Optional<ConfirmationCode> findConfirmationCodeByCodeValue(String codeValue);

    /**
     * Save the given confirmation code to repository
     * @param code - code to save
     * @return the saved entity; will never be null.
     */
    <T extends ConfirmationCode> T save(ConfirmationCode code);

    /**
     * Delete confirmation code by id.
     * If the code with id does not exist, do nothing
     * @param id - code's id to delete the confirmation code
     */
    void deleteById(Long id);

    /**
     * Delete the ConfirmationCode that is associated with provided code value. Do nothing if the code does not exist
     * @param codeValue - code value that associated with ConfirmationCode
     */
    void deleteByCodeValue(String codeValue);

    /**
     * Return number of codes in repository
     * @return - number of confirmation codes
     */
    long count();
}
