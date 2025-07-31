package com.terran.library.service;

import com.terran.library.model.Borrower;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing borrowers.
 * 
 * @author Derry Terran
 */
public interface BorrowerService {
    
    /**
     * Register a new borrower.
     * 
     * @param borrower the borrower to register
     * @return the registered borrower with ID
     * @throws IllegalArgumentException if a borrower with the same email already exists
     */
    Borrower registerBorrower(Borrower borrower);
    
    /**
     * Get a borrower by ID.
     * 
     * @param id the borrower ID
     * @return an Optional containing the borrower if found, or empty if not found
     */
    Optional<Borrower> getBorrowerById(Long id);
    
    /**
     * Get a borrower by email.
     * 
     * @param email the borrower email
     * @return an Optional containing the borrower if found, or empty if not found
     */
    Optional<Borrower> getBorrowerByEmail(String email);
    
    /**
     * Get all borrowers.
     * 
     * @return a list of all borrowers
     */
    List<Borrower> getAllBorrowers();
}