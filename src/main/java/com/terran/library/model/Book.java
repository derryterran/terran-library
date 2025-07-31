package com.terran.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing a book in the library.
 * Each book has a unique ID, ISBN number, title, and author.
 * Books with the same ISBN, title, and author are tracked with a stock count.
 * 
 * @author Derry Terran
 */
@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "ISBN is required")
    @Column(nullable = false)
    private String isbn;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Author is required")
    @Column(nullable = false)
    private String author;
    /**
     * The relationship to Borrower uses EAGER fetching to prevent LazyInitializationException
     * when accessing the borrower outside of a transaction context, particularly during
     * book borrowing and returning operations.
     * 
     * The optional=true attribute is explicitly set to ensure proper handling of null values
     * during transaction commits, preventing potential JPA transaction errors.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "borrower_id", nullable = true)
    private Borrower borrower;
    
    /**
     * Checks if the book is currently borrowed.
     * 
     * @return true if the book is borrowed, false otherwise
     */
    public boolean isBorrowed() {
        return borrower != null;
    }
}