package com.terran.library.actuator;

import com.terran.library.repository.BookRepository;
import com.terran.library.repository.BorrowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom health indicator for the library application.
 * Checks if the repositories are accessible and reports the number of books and borrowers.
 * 
 * @author Derry Terran
 */
@Component
public class LibraryHealthIndicator implements HealthIndicator {

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;

    @Autowired
    public LibraryHealthIndicator(BookRepository bookRepository, BorrowerRepository borrowerRepository) {
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
    }

    @Override
    public Health health() {
        try {
            var bookCount = bookRepository.count();
            var borrowerCount = borrowerRepository.count();
            var borrowedBooksCount = bookRepository.findByBorrowerIsNotNull().size();

            return Health.up()
                    .withDetail("totalBooks", bookCount)
                    .withDetail("totalBorrowers", borrowerCount)
                    .withDetail("borrowedBooks", borrowedBooksCount)
                    .withDetail("availableBooks", bookCount - borrowedBooksCount)
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}