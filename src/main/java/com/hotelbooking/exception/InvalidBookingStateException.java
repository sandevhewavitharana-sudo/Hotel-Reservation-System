package com.hotelbooking.exception;

/** Thrown when an operation is attempted against a booking that's in the wrong state for it (e.g. checking in a cancelled booking). */
public class InvalidBookingStateException extends RuntimeException {
    public InvalidBookingStateException(String message) {
        super(message);
    }
}
