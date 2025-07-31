package com.terran.library.service;

import com.terran.library.model.Book;
import com.terran.library.model.Borrower;
import com.terran.library.repository.BookRepository;
import com.terran.library.repository.BorrowerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BookServiceImpl bookService;

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
    void registerBook_Success() {
        // Arrange
        Book newBook = new Book();
        newBook.setIsbn("5555555555");
        newBook.setTitle("New Book");
        newBook.setAuthor("New Author");
        newBook.setBorrower(borrower); // Should be set to null during registration

        when(bookRepository.findByIsbnAndTitleAndAuthor(anyString(), anyString(), anyString()))
            .thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book savedBook = invocation.getArgument(0);
            assertNull(savedBook.getBorrower()); // Verify borrower is set to null
            return savedBook;
        });

        // Act
        Book result = bookService.registerBook(newBook);

        // Assert
        assertNotNull(result);
        assertNull(result.getBorrower());
        verify(bookRepository, times(1)).findByIsbnAndTitleAndAuthor(
            newBook.getIsbn(), newBook.getTitle(), newBook.getAuthor());
        verify(bookRepository, times(1)).save(newBook);
    }
    
    @Test
    void registerBook_ExistingBook_IncrementStock() {
        // Arrange
        Book newBook = new Book();
        newBook.setIsbn("1234567890");
        newBook.setTitle("Spring Boot in Action");
        newBook.setAuthor("Craig Walls");
        
        when(bookRepository.findByIsbnAndTitleAndAuthor("1234567890", "Spring Boot in Action", "Craig Walls"))
            .thenReturn(Optional.of(book1)); // book1 already exists with stock 2
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Book result = bookService.registerBook(newBook);
        
        // Assert
        assertNotNull(result);
        verify(bookRepository, times(1)).findByIsbnAndTitleAndAuthor(
            newBook.getIsbn(), newBook.getTitle(), newBook.getAuthor());
        verify(bookRepository, times(1)).save(book1);
    }

    @Test
    void getBookById_Found() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        // Act
        Optional<Book> result = bookService.getBookById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(book1.getId(), result.get().getId());
        assertEquals(book1.getTitle(), result.get().getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void getBookById_NotFound() {
        // Arrange
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Book> result = bookService.getBookById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(bookRepository, times(1)).findById(99L);
    }

    @Test
    void getAllBooks() {
        // Arrange
        List<Book> books = Arrays.asList(book1, book2, book3);
        when(bookRepository.findAll()).thenReturn(books);

        // Act
        List<Book> result = bookService.getAllBooks();

        // Assert
        assertEquals(3, result.size());
        assertTrue(result.contains(book1));
        assertTrue(result.contains(book2));
        assertTrue(result.contains(book3));
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBooksByIsbn() {
        // Arrange
        List<Book> booksWithSameIsbn = Arrays.asList(book1, book3);
        when(bookRepository.findByIsbn("1234567890")).thenReturn(booksWithSameIsbn);

        // Act
        List<Book> result = bookService.getBooksByIsbn("1234567890");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(book1));
        assertTrue(result.contains(book3));
        verify(bookRepository, times(1)).findByIsbn("1234567890");
    }

    @Test
    void borrowBook_Success() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Remember initial stock

        // Act
        Book result = bookService.borrowBook(1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(borrower, result.getBorrower());
        verify(bookRepository, times(1)).findById(1L);
        verify(borrowerRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(book1);
    }
    
    @Test
    void borrowBook_OutOfStock() {
        // Arrange
        Book outOfStockBook = new Book();
        outOfStockBook.setId(4L);
        outOfStockBook.setIsbn("1111111111");
        outOfStockBook.setTitle("Out of Stock Book");
        outOfStockBook.setAuthor("Test Author");

        when(bookRepository.findById(4L)).thenReturn(Optional.of(outOfStockBook));
        
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.borrowBook(4L, 1L);
        });
        
        assertTrue(exception.getMessage().contains("out of stock"));
        verify(bookRepository, times(1)).findById(4L);
        verify(borrowerRepository, never()).findById(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void borrowBook_BookNotFound() {
        // Arrange
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.borrowBook(99L, 1L);
        });

        assertTrue(exception.getMessage().contains("Book not found"));
        verify(bookRepository, times(1)).findById(99L);
        verify(borrowerRepository, never()).findById(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void borrowBook_BorrowerNotFound() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(borrowerRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.borrowBook(1L, 99L);
        });

        assertTrue(exception.getMessage().contains("Borrower not found"));
        verify(bookRepository, times(1)).findById(1L);
        verify(borrowerRepository, times(1)).findById(99L);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void borrowBook_AlreadyBorrowed() {
        // Arrange
        when(bookRepository.findById(3L)).thenReturn(Optional.of(book3)); // book3 is already borrowed

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.borrowBook(3L, 1L);
        });

        assertTrue(exception.getMessage().contains("already borrowed"));
        verify(bookRepository, times(1)).findById(3L);
        verify(borrowerRepository, never()).findById(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void returnBook_Success() {
        // Arrange
        when(bookRepository.findById(3L)).thenReturn(Optional.of(book3)); // book3 is borrowed
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book savedBook = invocation.getArgument(0);
            assertNull(savedBook.getBorrower()); // Verify borrower is set to null
            return savedBook;
        });

        // Remember initial stock

        // Act
        Book result = bookService.returnBook(3L);

        // Assert
        assertNotNull(result);
        assertNull(result.getBorrower());
        verify(bookRepository, times(1)).findById(3L);
        verify(bookRepository, times(1)).save(book3);
    }

    @Test
    void returnBook_BookNotFound() {
        // Arrange
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.returnBook(99L);
        });

        assertTrue(exception.getMessage().contains("Book not found"));
        verify(bookRepository, times(1)).findById(99L);
        verify(bookRepository, never()).save(any());
    }

    @Test
    void returnBook_NotBorrowed() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1)); // book1 is not borrowed

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.returnBook(1L);
        });

        assertTrue(exception.getMessage().contains("not borrowed"));
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).save(any());
    }
}