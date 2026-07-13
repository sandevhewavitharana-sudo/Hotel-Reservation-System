package com.hotelbooking.repository;

import com.hotelbooking.model.Booking;
import com.hotelbooking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByGuestId(Long guestId);

    List<Booking> findByRoomId(Long roomId);

    List<Booking> findByStatus(BookingStatus status);

    /**
     * The core "is this room free?" query. Two date ranges [a1,a2) and
     * [b1,b2) overlap unless a2 <= b1 or b2 <= a1 - so we look for any
     * *active* booking on this room that breaks that non-overlap rule.
     * Cancelled bookings don't block anything, so they're excluded.
     */
    @Query("""
            SELECT b FROM Booking b
            WHERE b.room.id = :roomId
              AND b.status <> com.hotelbooking.model.BookingStatus.CANCELLED
              AND b.checkInDate < :checkOutDate
              AND :checkInDate < b.checkOutDate
            """)
    List<Booking> findOverlappingBookings(
            @Param("roomId") Long roomId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate
    );
}
