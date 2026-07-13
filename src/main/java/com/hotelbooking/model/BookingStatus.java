package com.hotelbooking.model;

/**
 * Lifecycle of a booking, in the order a normal stay progresses through.
 * CANCELLED can be reached from PENDING or CONFIRMED, but never from
 * CHECKED_OUT - that transition is rejected in BookingServiceImpl.
 */
public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED
}
