package com.hotelbooking.exception;

/** Thrown when a booking request collides with an existing booking or an offline room. */
public class RoomNotAvailableException extends RuntimeException {
    public RoomNotAvailableException(String message) {
        super(message);
    }
}
