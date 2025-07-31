package com.terran.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terran.library.model.Book;
import com.terran.library.model.Borrower;
import com.terran.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    private Book book1;
    private Book book2;
    private Book book3;
    private Borrower borrower;

    @BeforeEach
    void setUp() {
        // Set up borrower
        borrower = new Borrower();
        borrower.setId(1L);
        borrower.setName("John Doe");
        borrower.setEmail("john.doe@example.com");

        // Set up books
        book1 = new Book();
        book1.setId(1L);
        book1.setIsbn("1234567890");
        book1.setTitle("Spring Boot in Action");
        book1.setAuthor("Craig Walls");
        book1.setBorrower(null);

        book2 = new Book();
        book2.setId(2L);
        book2.setIsbn("0987654321");
        book2.setTitle("Clean Code");
        book2.setAuthor("Robert C. Martin");
        book2.setBorrower(null);

        book3 = new Book();
        book3.setId(3L);
        book3.setIsbn("1234567890"); // Same ISBN as book1 (multiple copies)
        book3.setTitle("Spring Boot in Action");
        book3.setAuthor("Craig Walls");
        book3.setBorrower(borrower); // Already borrowed
    }

    @Test
    void registerBook_Success() throws Exception {
        // Arrange
        Book newBook = new Book();
        newBook.setIsbn("5555555555");
        newBook.setTitle("New Book");
        newBook.setAuthor("New Author");

        Book savedBook = new Book();
        savedBook.setId(4L);
        savedBook.setIsbn(newBook.getIsbn());
        savedBook.setTitle(newBook.getTitle());
        savedBook.setAuthor(newBook.getAuthor());
        savedBook.setBorrower(null);

        when(bookService.registerBook(any(Book.class))).thenReturn(savedBook);

        // Act & Assert
        mockMvc.perform(post("/terranapi/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.isbn", is("5555555555")))
                .andExpect(jsonPath("$.title", is("New Book")))
                .andExpect(jsonPath("$.author", is("New Author")));

        verify(bookService, times(1)).registerBook(any(Book.class));
    }

    @Test
    void getAllBooks() throws Exception {
        // Arrange
        List<Book> books = Arrays.asList(book1, book2, book3);
        when(bookService.getAllBooks()).thenReturn(books);

        // Act & Assert
        mockMvc.perform(get("/terranapi/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Spring Boot in Action")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Clean Code")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].title", is("Spring Boot in Action")));

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void getBookById_Found() throws Exception {
        // Arrange
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book1));

        // Act & Assert
        mockMvc.perform(get("/terranapi/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.isbn", is("1234567890")))
                .andExpect(jsonPath("$.title", is("Spring Boot in Action")))
                .andExpect(jsonPath("$.author", is("Craig Walls")));

        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void getBookById_NotFound() throws Exception {
        // Arrange
        when(bookService.getBookById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/terranapi/books/99"))
                .andExpect(status().isNotFound());

        verify(bookService, times(1)).getBookById(99L);
    }

    @Test
    void borrowBook_Success() throws Exception {
        // Arrange
        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("borrowerId", 1L);

        Book borrowedBook = new Book();
        borrowedBook.setId(1L);
        borrowedBook.setIsbn("1234567890");
        borrowedBook.setTitle("Spring Boot in Action");
        borrowedBook.setAuthor("Craig Walls");
        borrowedBook.setBorrower(borrower);

        when(bookService.borrowBook(1L, 1L)).thenReturn(borrowedBook);

        // Act & Assert
        mockMvc.perform(post("/terranapi/books/1/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.borrower.id", is(1)))
                .andExpect(jsonPath("$.borrower.name", is("John Doe")));

        verify(bookService, times(1)).borrowBook(1L, 1L);
    }

    @Test
    void borrowBook_MissingBorrowerId() throws Exception {
        // Arrange
        Map<String, String> requestBody = new HashMap<>(); // Empty request body

        // Act & Assert
        mockMvc.perform(post("/terranapi/books/1/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).borrowBook(anyLong(), anyLong());
    }

    @Test
    void borrowBook_BookAlreadyBorrowed() throws Exception {
        // Arrange
        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("borrowerId", 1L);

        when(bookService.borrowBook(3L, 1L))
                .thenThrow(new IllegalArgumentException("Book is already borrowed"));

        // Act & Assert
        mockMvc.perform(post("/terranapi/books/3/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());

        verify(bookService, times(1)).borrowBook(3L, 1L);
    }

    @Test
    void returnBook_Success() throws Exception {
        // Arrange
        Book returnedBook = new Book();
        returnedBook.setId(3L);
        returnedBook.setIsbn("1234567890");
        returnedBook.setTitle("Spring Boot in Action");
        returnedBook.setAuthor("Craig Walls");
        returnedBook.setBorrower(null);

        when(bookService.returnBook(3L)).thenReturn(returnedBook);

        // Act & Assert
        mockMvc.perform(post("/terranapi/books/3/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.borrower").doesNotExist());

        verify(bookService, times(1)).returnBook(3L);
    }

    @Test
    void returnBook_NotBorrowed() throws Exception {
        // Arrange
        when(bookService.returnBook(1L))
                .thenThrow(new IllegalArgumentException("Book is not borrowed"));

        // Act & Assert
        mockMvc.perform(post("/terranapi/books/1/return"))
                .andExpect(status().isBadRequest());

        verify(bookService, times(1)).returnBook(1L);
    }
    
    @Test
    void borrowBook_DirectUrl_Success() throws Exception {
        // Arrange
        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("borrowerId", 1L);

        Book borrowedBook = new Book();
        borrowedBook.setId(1L);
        borrowedBook.setIsbn("1234567890");
        borrowedBook.setTitle("Spring Boot in Action");
        borrowedBook.setAuthor("Craig Walls");
        borrowedBook.setBorrower(borrower);

        when(bookService.borrowBook(1L, 1L)).thenReturn(borrowedBook);

        // Act & Assert
        mockMvc.perform(post("/terranapi/1/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.borrower.id", is(1)))
                .andExpect(jsonPath("$.borrower.name", is("John Doe")));

        verify(bookService, times(1)).borrowBook(1L, 1L);
    }

    @Test
    void returnBook_DirectUrl_Success() throws Exception {
        // Arrange
        Book returnedBook = new Book();
        returnedBook.setId(3L);
        returnedBook.setIsbn("1234567890");
        returnedBook.setTitle("Spring Boot in Action");
        returnedBook.setAuthor("Craig Walls");
        returnedBook.setBorrower(null);

        when(bookService.returnBook(3L)).thenReturn(returnedBook);

        // Act & Assert
        mockMvc.perform(post("/terranapi/3/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.borrower").doesNotExist());

        verify(bookService, times(1)).returnBook(3L);
    }
}