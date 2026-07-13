package com.hotelbooking.service.impl;

import com.hotelbooking.dto.BookingRequest;
import com.hotelbooking.exception.InvalidBookingStateException;
import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.exception.RoomNotAvailableException;
import com.hotelbooking.model.Booking;
import com.hotelbooking.model.BookingStatus;
import com.hotelbooking.model.Guest;
import com.hotelbooking.model.Room;
import com.hotelbooking.model.RoomStatus;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.GuestRepository;
import com.hotelbooking.repository.RoomRepository;
import com.hotelbooking.service.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    /** Points awarded per completed stay - a simple loyalty rule, easy to swap for something smarter later. */
    private static final int LOYALTY_POINTS_PER_STAY = 10;

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                               RoomRepository roomRepository,
                               GuestRepository guestRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
    }

    @Override
    @Transactional
    public Booking createBooking(BookingRequest request) {
        Guest guest = guestRepository.findById(request.getGuestId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id " + request.getGuestId()));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id " + request.getRoomId()));

        if (!request.getCheckInDate().isBefore(request.getCheckOutDate())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        if (!room.isBookable()) {
            throw new RoomNotAvailableException("Room " + room.getRoomNumber() + " is not currently bookable (status: " + room.getStatus() + ")");
        }

        if (request.getNumberOfGuests() > room.getRoomType().getMaxOccupancy()) {
            throw new IllegalArgumentException(
                    "Room type " + room.getRoomType() + " allows a maximum of "
                            + room.getRoomType().getMaxOccupancy() + " guests");
        }

        // The actual double-booking guard: reject if any active booking on
        // this room overlaps the requested date range.
        List<Booking> overlaps = bookingRepository.findOverlappingBookings(
                room.getId(), request.getCheckInDate(), request.getCheckOutDate());
        if (!overlaps.isEmpty()) {
            throw new RoomNotAvailableException(
                    "Room " + room.getRoomNumber() + " is already booked for part of that date range");
        }

        Booking booking = new Booking(guest, room, request.getCheckInDate(), request.getCheckOutDate(), request.getNumberOfGuests());
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + id));
    }

    @Override
    public List<Booking> getBookingsForGuest(Long guestId) {
        return bookingRepository.findByGuestId(guestId);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking confirmBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        requireStatus(booking, BookingStatus.PENDING, "confirm");
        booking.setStatus(BookingStatus.CONFIRMED);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking checkIn(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        requireStatus(booking, BookingStatus.CONFIRMED, "check in");

        booking.setStatus(BookingStatus.CHECKED_IN);
        Room room = booking.getRoom();
        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking checkOut(Long bookingId) {
        Booking booking = getBookingById(bookingId);
        requireStatus(booking, BookingStatus.CHECKED_IN, "check out");

        booking.setStatus(BookingStatus.CHECKED_OUT);
        Room room = booking.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        Guest guest = booking.getGuest();
        guest.earnLoyaltyPoints(LOYALTY_POINTS_PER_STAY);
        guestRepository.save(guest);

        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = getBookingById(bookingId);

        if (booking.getStatus() == BookingStatus.CHECKED_OUT || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new InvalidBookingStateException(
                    "Cannot cancel a booking that is already " + booking.getStatus());
        }

        // If the guest had already checked in, cancelling also frees the room.
        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            Room room = booking.getRoom();
            room.setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(room);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }

    private void requireStatus(Booking booking, BookingStatus expected, String action) {
        if (booking.getStatus() != expected) {
            throw new InvalidBookingStateException(
                    "Cannot " + action + " a booking that is " + booking.getStatus() + " (expected " + expected + ")");
        }
    }
}
