package com.terran.library.service;

import com.terran.library.model.Borrower;
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
public class BorrowerServiceTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @InjectMocks
    private BorrowerServiceImpl borrowerService;

    private Borrower borrower1;
    private Borrower borrower2;

    @BeforeEach
    void setUp() {
        borrower1 = new Borrower();
        borrower1.setId(1L);
        borrower1.setName("John Doe");
        borrower1.setEmail("john.doe@example.com");

        borrower2 = new Borrower();
        borrower2.setId(2L);
        borrower2.setName("Jane Smith");
        borrower2.setEmail("jane.smith@example.com");
    }

    @Test
    void registerBorrower_Success() {
        // Arrange
        Borrower newBorrower = new Borrower();
        newBorrower.setName("New User");
        newBorrower.setEmail("new.user@example.com");

        when(borrowerRepository.existsByEmail(newBorrower.getEmail())).thenReturn(false);
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(newBorrower);

        // Act
        Borrower result = borrowerService.registerBorrower(newBorrower);

        // Assert
        assertNotNull(result);
        assertEquals(newBorrower.getName(), result.getName());
        assertEquals(newBorrower.getEmail(), result.getEmail());
        verify(borrowerRepository, times(1)).existsByEmail(newBorrower.getEmail());
        verify(borrowerRepository, times(1)).save(newBorrower);
    }

    @Test
    void registerBorrower_EmailAlreadyExists() {
        // Arrange
        Borrower newBorrower = new Borrower();
        newBorrower.setName("Duplicate User");
        newBorrower.setEmail("john.doe@example.com");

        when(borrowerRepository.existsByEmail(newBorrower.getEmail())).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            borrowerService.registerBorrower(newBorrower);
        });

        assertTrue(exception.getMessage().contains("already exists"));
        verify(borrowerRepository, times(1)).existsByEmail(newBorrower.getEmail());
        verify(borrowerRepository, never()).save(any(Borrower.class));
    }

    @Test
    void getBorrowerById_Found() {
        // Arrange
        when(borrowerRepository.findById(1L)).thenReturn(Optional.of(borrower1));

        // Act
        Optional<Borrower> result = borrowerService.getBorrowerById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(borrower1.getId(), result.get().getId());
        assertEquals(borrower1.getName(), result.get().getName());
        verify(borrowerRepository, times(1)).findById(1L);
    }

    @Test
    void getBorrowerById_NotFound() {
        // Arrange
        when(borrowerRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Borrower> result = borrowerService.getBorrowerById(99L);

        // Assert
        assertFalse(result.isPresent());
        verify(borrowerRepository, times(1)).findById(99L);
    }

    @Test
    void getBorrowerByEmail_Found() {
        // Arrange
        when(borrowerRepository.findByEmail(borrower1.getEmail())).thenReturn(Optional.of(borrower1));

        // Act
        Optional<Borrower> result = borrowerService.getBorrowerByEmail(borrower1.getEmail());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(borrower1.getId(), result.get().getId());
        assertEquals(borrower1.getEmail(), result.get().getEmail());
        verify(borrowerRepository, times(1)).findByEmail(borrower1.getEmail());
    }

    @Test
    void getBorrowerByEmail_NotFound() {
        // Arrange
        String nonExistentEmail = "nonexistent@example.com";
        when(borrowerRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // Act
        Optional<Borrower> result = borrowerService.getBorrowerByEmail(nonExistentEmail);

        // Assert
        assertFalse(result.isPresent());
        verify(borrowerRepository, times(1)).findByEmail(nonExistentEmail);
    }

    @Test
    void getAllBorrowers() {
        // Arrange
        List<Borrower> borrowers = Arrays.asList(borrower1, borrower2);
        when(borrowerRepository.findAll()).thenReturn(borrowers);

        // Act
        List<Borrower> result = borrowerService.getAllBorrowers();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(borrower1));
        assertTrue(result.contains(borrower2));
        verify(borrowerRepository, times(1)).findAll();
    }
}