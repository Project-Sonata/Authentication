package com.odeyalo.sonata.authentication.repository;

import com.odeyalo.sonata.authentication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link UserRepository} sub-interface with JPA support.
 */
@Repository
public interface JpaSupportUserRepository extends UserRepository, JpaRepository<User, Long> {
}
