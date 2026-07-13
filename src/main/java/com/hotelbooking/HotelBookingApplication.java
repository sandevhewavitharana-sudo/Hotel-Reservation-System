package com.hotelbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Hotel Booking System.
 *
 * This is a layered Spring Boot application:
 *   model      -> entities (Room, Guest, Booking, Payment ...)
 *   repository -> Spring Data JPA interfaces (database access)
 *   service    -> business logic (interfaces + implementations)
 *   controller -> REST endpoints + Thymeleaf web pages
 *   dto        -> request/response objects used at the controller boundary
 */
@SpringBootApplication
public class HotelBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelBookingApplication.class, args);
    }
}
