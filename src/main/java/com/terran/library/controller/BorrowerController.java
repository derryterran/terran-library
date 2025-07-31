package com.terran.library.controller;

import com.terran.library.model.Borrower;
import com.terran.library.service.BorrowerService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing borrowers.
 * 
 * @author Derry Terran
 */
@RestController
@RequestMapping("/terranapi/borrowers")
@Tag(name = "Borrower", description = "Borrower management APIs")
public class BorrowerController {

    private final BorrowerService borrowerService;

    @Autowired
    public BorrowerController(BorrowerService borrowerService) {
        this.borrowerService = borrowerService;
    }

    /**
     * Register a new borrower.
     *
     * @param borrower the borrower to register
     * @return the registered borrower with ID
     */
    @PostMapping
    @Operation(summary = "Register a new borrower", description = "Registers a new borrower to the library")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Borrower registered successfully",
                    content = @Content(schema = @Schema(implementation = Borrower.class))),
            @ApiResponse(responseCode = "400", description = "Invalid borrower data or email already exists",
                    content = @Content)
    })
    public ResponseEntity<Borrower> registerBorrower(@Valid @RequestBody Borrower borrower) {
        try {
            var registeredBorrower = borrowerService.registerBorrower(borrower);
            return new ResponseEntity<>(registeredBorrower, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get all borrowers.
     *
     * @return a list of all borrowers
     */
    @GetMapping
    @Operation(summary = "Get all borrowers", description = "Returns a list of all borrowers in the library")
    @ApiResponse(responseCode = "200", description = "List of borrowers retrieved successfully",
            content = @Content(schema = @Schema(implementation = Borrower.class)))
    public ResponseEntity<List<Borrower>> getAllBorrowers() {
        var borrowers = borrowerService.getAllBorrowers();
        return new ResponseEntity<>(borrowers, HttpStatus.OK);
    }

    /**
     * Get a borrower by ID.
     *
     * @param id the borrower ID
     * @return the borrower if found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a borrower by ID", description = "Returns a borrower with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Borrower found",
                    content = @Content(schema = @Schema(implementation = Borrower.class))),
            @ApiResponse(responseCode = "404", description = "Borrower not found",
                    content = @Content)
    })
    public ResponseEntity<Borrower> getBorrowerById(@PathVariable Long id) {
        return borrowerService.getBorrowerById(id)
                .map(borrower -> new ResponseEntity<>(borrower, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}