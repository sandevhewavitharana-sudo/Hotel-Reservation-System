package com.hotelbooking.service;

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

    public final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id " + id));

    }

    @Transactional
    public Room createRoom(Room room) {
        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            throw new RuntimeException("Room number already exists with" + room.getRoomNumber());
        }

        Room savedRoom = Room.builder()
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .PricePerNight(room.getPricePerNight())
                .capacity(room.getCapacity())
                .description(room.getDescription())
                .build();
        room = roomRepository.save(savedRoom);
        log.info("Room created with id: {}", room.getId());
        return room;
    }

    public Room addRoom(@Valid Room room) {
        return createRoom(room);
    }
}


