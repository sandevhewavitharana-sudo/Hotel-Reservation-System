package com.hotelbooking.exception;

/** Thrown when a lookup by id (room, guest, booking...) finds nothing. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
