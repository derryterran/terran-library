package com.terran.library;

import com.terran.library.model.Borrower;
import com.terran.library.repository.BorrowerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test for ID generation in database
 * Note: This test was originally designed for PostgreSQL but now works with any database
 * configured in the test profile (currently H2)
 */
@SpringBootTest
@ActiveProfiles("test")
public class PostgreSQLGeneratedKeysTest {

    @Autowired
    private BorrowerRepository borrowerRepository;

    @Test
    @Transactional
    public void testSaveBorrower() {
        // Create a new borrower
        Borrower borrower = new Borrower();
        borrower.setName("Test User");
        borrower.setEmail("test" + System.currentTimeMillis() + "@example.com");

        // Save the borrower
        Borrower savedBorrower = borrowerRepository.save(borrower);

        // Verify that the ID was generated
        assertNotNull(savedBorrower.getId(), "Borrower ID should not be null");
        
        System.out.println("Successfully saved borrower with ID: " + savedBorrower.getId());
    }
}