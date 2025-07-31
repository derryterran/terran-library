package com.terran.library.repository;

import com.terran.library.model.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Borrower entity.
 * Provides methods to interact with the borrowers table in the database.
 * 
 * @author Derry Terran
 */
@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, Long> {
    
    /**
     * Find a borrower by email.
     * 
     * @param email the email to search for
     * @return an Optional containing the borrower if found, or empty if not found
     */
    Optional<Borrower> findByEmail(String email);
    
    /**
     * Check if a borrower with the given email exists.
     * 
     * @param email the email to check
     * @return true if a borrower with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}