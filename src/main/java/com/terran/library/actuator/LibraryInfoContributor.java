package com.terran.library.actuator;

import com.terran.library.repository.BookRepository;
import com.terran.library.repository.BorrowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom info contributor for the library application.
 * Provides additional information about the library system.
 * 
 * @author Derry Terran
 */
@Component
public class LibraryInfoContributor implements InfoContributor {

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final LocalDateTime startTime = LocalDateTime.now();

    @Autowired
    public LibraryInfoContributor(BookRepository bookRepository, BorrowerRepository borrowerRepository) {
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
    }

    @Override
    public void contribute(Info.Builder builder) {
        var libraryDetails = new HashMap<>();
        
        // Library statistics
        libraryDetails.put("totalBooks", bookRepository.count());
        libraryDetails.put("totalBorrowers", borrowerRepository.count());
        
        // System information
        var systemInfo = new HashMap<>();
        
        builder.withDetail("library", libraryDetails)
               .withDetail("system", systemInfo);
    }
}