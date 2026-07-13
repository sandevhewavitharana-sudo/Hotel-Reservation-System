package com.hotelbooking.model;

/**
 * Operational state of a physical room, independent of whether it has
 * future bookings. A room can be AVAILABLE today but already booked for
 * next week - that distinction is handled by checking the Booking table,
 * not this field. This field is for things staff control directly.
 */
public enum RoomStatus {
    AVAILABLE,
    OCCUPIED,
    MAINTENANCE,
    OUT_OF_SERVICE
}
