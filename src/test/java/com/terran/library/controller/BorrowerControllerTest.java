package com.terran.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.terran.library.model.Borrower;
import com.terran.library.service.BorrowerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BorrowerController.class)
public class BorrowerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BorrowerService borrowerService;

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
    void registerBorrower_Success() throws Exception {
        // Arrange
        Borrower newBorrower = new Borrower();
        newBorrower.setName("New User");
        newBorrower.setEmail("new.user@example.com");

        Borrower savedBorrower = new Borrower();
        savedBorrower.setId(3L);
        savedBorrower.setName(newBorrower.getName());
        savedBorrower.setEmail(newBorrower.getEmail());

        when(borrowerService.registerBorrower(any(Borrower.class))).thenReturn(savedBorrower);

        // Act & Assert
        mockMvc.perform(post("/terranapi/borrowers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBorrower)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("New User")))
                .andExpect(jsonPath("$.email", is("new.user@example.com")));

        verify(borrowerService, times(1)).registerBorrower(any(Borrower.class));
    }

    @Test
    void registerBorrower_EmailAlreadyExists() throws Exception {
        // Arrange
        Borrower newBorrower = new Borrower();
        newBorrower.setName("Duplicate User");
        newBorrower.setEmail("john.doe@example.com");

        when(borrowerService.registerBorrower(any(Borrower.class)))
                .thenThrow(new IllegalArgumentException("A borrower with email john.doe@example.com already exists"));

        // Act & Assert
        mockMvc.perform(post("/terranapi/borrowers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBorrower)))
                .andExpect(status().isBadRequest()); // IllegalArgumentException is handled as BAD_REQUEST (400)

        verify(borrowerService, times(1)).registerBorrower(any(Borrower.class));
    }

    @Test
    void getAllBorrowers() throws Exception {
        // Arrange
        List<Borrower> borrowers = Arrays.asList(borrower1, borrower2);
        when(borrowerService.getAllBorrowers()).thenReturn(borrowers);

        // Act & Assert
        mockMvc.perform(get("/terranapi/borrowers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John Doe")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Jane Smith")));

        verify(borrowerService, times(1)).getAllBorrowers();
    }

    @Test
    void getBorrowerById_Found() throws Exception {
        // Arrange
        when(borrowerService.getBorrowerById(1L)).thenReturn(Optional.of(borrower1));

        // Act & Assert
        mockMvc.perform(get("/terranapi/borrowers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));

        verify(borrowerService, times(1)).getBorrowerById(1L);
    }

    @Test
    void getBorrowerById_NotFound() throws Exception {
        // Arrange
        when(borrowerService.getBorrowerById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/terranapi/borrowers/99"))
                .andExpect(status().isNotFound());

        verify(borrowerService, times(1)).getBorrowerById(99L);
    }
}