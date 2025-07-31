package com.terran.library.controller;

import com.terran.library.model.Book;
import com.terran.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing books.
 * 
 * @author Derry Terran
 */
@RestController
@Tag(name = "Book", description = "Book management APIs")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Register a new book.
     *
     * @param book the book to register
     * @return the registered book with ID
     */
    @PostMapping({"/terranapi/book", "/terranapi/book/"})
    @Operation(summary = "Register a new book", description = "Registers a new book to the library")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book registered successfully",
                    content = @Content(schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "400", description = "Invalid book data, Please double check ISBN, Author and Book Title",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> registerBook(@Valid @RequestBody Book book) {
        var registeredBook = bookService.registerBook(book);
        if (registeredBook != null) {
            return new ResponseEntity<>(registeredBook, HttpStatus.CREATED);
        } else {
            var errorResponse = new HashMap<>();
            errorResponse.put("error", "Book registration failed");
            errorResponse.put("message", "Invalid book data, Please double check ISBN, Author and Book Title");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all books.
     *
     * @return a list of all books
     */
    @GetMapping("/terranapi/books")
    @Operation(summary = "Get all books", description = "Returns a list of all books in the library")
    @ApiResponse(responseCode = "200", description = "List of books retrieved successfully",
            content = @Content(schema = @Schema(implementation = Book.class)))
    public ResponseEntity<List<Book>> getAllBooks() {
        var books = bookService.getAllBooks();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    /**
     * Get a book by ID.
     *
     * @param id the book ID
     * @return the book if found
     */
    @GetMapping("/terranapi/books/{id}")
    @Operation(summary = "Get a book by ID", description = "Returns a book with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found",
                    content = @Content(schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content)
    })
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(book -> new ResponseEntity<>(book, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Borrow a book.
     *
     * @param id the book ID
     * @param requestBody the request body containing the borrower ID
     * @return the borrowed book
     */
    @PostMapping({"/terranapi/books/{id}/borrow", "/terranapi/{id}/borrow"})
    @Operation(summary = "Borrow a book", description = "Borrows a book with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book borrowed successfully",
                    content = @Content(schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "400", description = "Book is already borrowed or invalid request",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book or borrower not found",
                    content = @Content)
    })
    public ResponseEntity<Book> borrowBook(@PathVariable Long id, @RequestBody Map<String, Long> requestBody) {
        try {
            var borrowerId = requestBody.get("borrowerId");
            if (borrowerId == null) {
                throw new IllegalArgumentException("Borrower ID is required");
            }
            
            var borrowedBook = bookService.borrowBook(id, borrowerId);
            return new ResponseEntity<>(borrowedBook, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Return a book.
     *
     * @param id the book ID
     * @return the returned book
     */
    @PostMapping({"/terranapi/books/{id}/return", "/terranapi/{id}/return"})
    @Operation(summary = "Return a book", description = "Returns a borrowed book with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book returned successfully",
                    content = @Content(schema = @Schema(implementation = Book.class))),
            @ApiResponse(responseCode = "400", description = "Book is not borrowed or invalid request",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content)
    })
    public ResponseEntity<Book> returnBook(@PathVariable Long id) {
        try {
            var returnedBook = bookService.returnBook(id);
            return new ResponseEntity<>(returnedBook, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}