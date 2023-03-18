package com.odeyalo.sonata.authentication.repository;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;

import java.util.Optional;

/**
 * {@link ConfirmationCodeRepository} implementation that LITERALLY DOES NOTHING
 */
public class NullConfirmationCodeRepositoryStub implements ConfirmationCodeRepository {
    @Override
    public Optional<ConfirmationCode> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<ConfirmationCode> findConfirmationCodeByCodeValue(String codeValue) {
        return Optional.empty();
    }

    @Override
    public <T extends ConfirmationCode> T save(ConfirmationCode code) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void deleteByCodeValue(String codeValue) {

    }

    @Override
    public long count() {
        return 0;
    }
}
