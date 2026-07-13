package com.hotelbooking.service;

import com.hotelbooking.dto.BookingRequest;
import com.hotelbooking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(BookingRequest request);
    Booking getBookingById(Long id);
    List<Booking> getBookingsForGuest(Long guestId);
    List<Booking> getAllBookings();
    Booking confirmBooking(Long bookingId);
    Booking checkIn(Long bookingId);
    Booking checkOut(Long bookingId);
    Booking cancelBooking(Long bookingId);
}
