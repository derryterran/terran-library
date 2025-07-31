package com.terran.library.service;

import com.terran.library.model.Book;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing books.
 * 
 * @author Derry Terran
 */
public interface BookService {
    
    /**
     * Register a new book to the library.
     * 
     * @param book the book to register
     * @return the registered book with ID
     */
    Book registerBook(Book book);
    
    /**
     * Get a book by ID.
     * 
     * @param id the book ID
     * @return an Optional containing the book if found, or empty if not found
     */
    Optional<Book> getBookById(Long id);
    
    /**
     * Get all books in the library.
     * 
     * @return a list of all books
     */
    List<Book> getAllBooks();
    
    /**
     * Get all books with a specific ISBN.
     * 
     * @param isbn the ISBN to search for
     * @return a list of books with the given ISBN
     */
    List<Book> getBooksByIsbn(String isbn);
    
    /**
     * Borrow a book with a particular book ID.
     * 
     * @param bookId the ID of the book to borrow
     * @param borrowerId the ID of the borrower
     * @return the borrowed book
     * @throws IllegalArgumentException if the book is already borrowed or doesn't exist,
     *         or if the borrower doesn't exist
     */
    Book borrowBook(Long bookId, Long borrowerId);
    
    /**
     * Return a borrowed book.
     * 
     * @param bookId the ID of the book to return
     * @return the returned book
     * @throws IllegalArgumentException if the book is not borrowed or doesn't exist
     */
    Book returnBook(Long bookId);
}