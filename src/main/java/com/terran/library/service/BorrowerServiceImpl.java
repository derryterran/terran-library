package com.terran.library.service;

import com.terran.library.model.Borrower;
import com.terran.library.repository.BorrowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the BorrowerService interface.
 * 
 * @author Derry Terran
 */
@Service
@Transactional
public class BorrowerServiceImpl implements BorrowerService {

    private final BorrowerRepository borrowerRepository;

    @Autowired
    public BorrowerServiceImpl(BorrowerRepository borrowerRepository) {
        this.borrowerRepository = borrowerRepository;
    }

    @Override
    public Borrower registerBorrower(Borrower borrower) {
        // Check if a borrower with the same email already exists
        if (borrowerRepository.existsByEmail(borrower.getEmail())) {
            throw new IllegalArgumentException("A borrower with email " + borrower.getEmail() + " already exists");
        }
        
        return borrowerRepository.save(borrower);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Borrower> getBorrowerById(Long id) {
        return borrowerRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Borrower> getBorrowerByEmail(String email) {
        return borrowerRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Borrower> getAllBorrowers() {
        return borrowerRepository.findAll();
    }
}