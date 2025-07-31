package com.terran.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Terran Library Management System.
 * This Spring Boot application provides REST APIs for managing books and borrowers.
 * 
 * @author Derry Terran
 */
@SpringBootApplication
public class TerranLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(TerranLibraryApplication.class, args);
    }
}