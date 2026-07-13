package com.hotelbooking.service.impl;

import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.model.Booking;
import com.hotelbooking.model.Room;
import com.hotelbooking.model.RoomStatus;
import com.hotelbooking.model.RoomType;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.RoomRepository;
import com.hotelbooking.service.RoomService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public RoomServiceImpl(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Room addRoom(Room room) {
        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            throw new IllegalArgumentException("Room number " + room.getRoomNumber() + " already exists");
        }
        return roomRepository.save(room);
    }

    @Override
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id " + id));
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    /**
     * A room is "available for these dates" if it's operationally usable
     * (not under maintenance) AND has no active booking whose date range
     * overlaps the requested one. roomType is optional - null means "any".
     */
    @Override
    public List<Room> searchAvailableRooms(LocalDate checkIn, LocalDate checkOut, RoomType roomType) {
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        List<Room> candidates = roomType == null
                ? roomRepository.findAll()
                : roomRepository.findByRoomType(roomType);

        return candidates.stream()
                .filter(room -> room.getStatus() != RoomStatus.MAINTENANCE
                        && room.getStatus() != RoomStatus.OUT_OF_SERVICE)
                .filter(room -> isFreeForDates(room, checkIn, checkOut))
                .collect(Collectors.toList());
    }

    private boolean isFreeForDates(Room room, LocalDate checkIn, LocalDate checkOut) {
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(room.getId(), checkIn, checkOut);
        return overlapping.isEmpty();
    }

    @Override
    public Room updateRoomStatus(Long id, RoomStatus status) {
        Room room = getRoomById(id);
        room.setStatus(status);
        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(Long id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
    }
}
