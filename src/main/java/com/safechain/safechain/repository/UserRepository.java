package com.safechain.safechain.repository;

import com.safechain.safechain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     * @param email user's email
     * @return Optional<User> - user if found, empty if not
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if user exists by email
     * @param email user's email
     * @return boolean - true if exists, false otherwise
     */
    boolean existsByEmail(String email);
}
