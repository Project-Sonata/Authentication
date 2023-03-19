package com.odeyalo.sonata.authentication.repository;

import com.odeyalo.sonata.authentication.entity.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaConfirmationCodeRepository extends ConfirmationCodeRepository, JpaRepository<ConfirmationCode, Long> {
}
