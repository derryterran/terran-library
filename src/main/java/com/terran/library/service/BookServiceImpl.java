package com.terran.library.service;

import com.terran.library.model.Book;
import com.terran.library.model.Borrower;
import com.terran.library.repository.BookRepository;
import com.terran.library.repository.BorrowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the BookService interface.
 * 
 * @author Derry Terran
 */
@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository, BorrowerRepository borrowerRepository) {
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
    }

    @Override
    public Book registerBook(Book book) {
        // Ensure the book is not borrowed when registered
        book.setBorrower(null);
        
        // Check if a book with the same ISBN, title, and author already exists
        var existingBook = bookRepository.findByIsbn(book.getIsbn());
        if(existingBook !=null && existingBook.isEmpty()){
            return bookRepository.save(book);
        }else{
            if(book.getTitle().contentEquals(existingBook.get(0).getTitle()) && book.getAuthor().contentEquals(existingBook.get(0).getAuthor())){
                return bookRepository.save(book);
            }
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getBooksByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    @Override
    public Book borrowBook(Long bookId, Long borrowerId) {
        // Get the book
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));
        
        // Check if the book is already borrowed
        if (book.isBorrowed()) {
            throw new IllegalArgumentException("Book is already borrowed");
        }
        
        // Get the borrower
        var borrower = borrowerRepository.findById(borrowerId)
                .orElseThrow(() -> new IllegalArgumentException("Borrower not found with ID: " + borrowerId));
        
        // Set the borrower and save the book
        book.setBorrower(borrower);
        return bookRepository.save(book);
    }

    @Override
    public Book returnBook(Long bookId) {
        // Get the book
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));
        
        // Check if the book is borrowed
        if (!book.isBorrowed()) {
            throw new IllegalArgumentException("Book is not borrowed");
        }
        
        // Remove the borrower and save the book
        book.setBorrower(null);
        return bookRepository.save(book);
    }
}