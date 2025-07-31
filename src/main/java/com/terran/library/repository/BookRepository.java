package com.terran.library.repository;

import com.terran.library.model.Book;
import com.terran.library.model.Borrower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Book entity.
 * Provides methods to interact with the books table in the database.
 * 
 * @author Derry Terran
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    /**
     * Find all books with a specific ISBN.
     * 
     * @param isbn the ISBN to search for
     * @return a list of books with the given ISBN
     */
    List<Book> findByIsbn(String isbn);
    
    /**
     * Find a book with a specific ISBN, title, and author.
     * 
     * @param isbn the ISBN to search for
     * @param title the title to search for
     * @param author the author to search for
     * @return an Optional containing the book if found, or empty if not found
     */
    Optional<Book> findByIsbnAndTitleAndAuthor(String isbn, String title, String author);
    
    /**
     * Find all books borrowed by a specific borrower.
     * 
     * @param borrower the borrower
     * @return a list of books borrowed by the given borrower
     */
    List<Book> findByBorrower(Borrower borrower);
    
    /**
     * Find all books that are not borrowed (available).
     * 
     * @return a list of available books
     */
    List<Book> findByBorrowerIsNull();
    
    /**
     * Find all books that are borrowed.
     * 
     * @return a list of borrowed books
     */
    List<Book> findByBorrowerIsNotNull();
    
    /**
     * Check if a book with the given ID is currently borrowed.
     * 
     * @param id the book ID
     * @return true if the book is borrowed, false otherwise
     */
    boolean existsByIdAndBorrowerIsNotNull(Long id);
}