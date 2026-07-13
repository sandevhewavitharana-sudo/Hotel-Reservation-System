package com.hotelbooking.service;

import com.hotelbooking.dto.response.CreateRoomRequest;
import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.model.Room;
import com.hotelbooking.repository.RoomRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room not found with id " + id));
    }

    @Transactional
    public Room createRoom(Room room) {

        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            throw new RuntimeException(
                    "Room number already exists: "
                            + room.getRoomNumber());
        }

        Room savedRoom = Room.builder()
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .pricePerNight(room.getPricePerNight())
                .capacity(room.getCapacity())
                .description(room.getDescription())
                .build();

        Room createdRoom = roomRepository.save(savedRoom);

        log.info("Room created with id: {}", createdRoom.getId());

        return createdRoom;
    }

    public Room addRoom(@Valid Room room) {
        return createRoom(room);
    }

    @Transactional
    public Room updateRoom(Long id, CreateRoomRequest request) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Room with id " + id + " not found"));

        room.setRoomType(request.getRoomType());
        room.setPricePerNight(request.getPricePerNight());
        room.setCapacity(request.getCapacity());
        room.setDescription(request.getDescription());

        Room updatedRoom = roomRepository.save(room);

        log.info("Room updated with id: {}", updatedRoom.getId());

        return updatedRoom;
    }
}