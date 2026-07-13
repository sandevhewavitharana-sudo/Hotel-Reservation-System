package com.hotelbooking.controller;

import com.hotelbooking.model.Room;
import com.hotelbooking.model.RoomStatus;
import com.hotelbooking.model.RoomType;
import com.hotelbooking.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<Room> addRoom(@Valid @RequestBody Room room) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.addRoom(room));
    }

    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/{id}")
    public Room getRoom(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    /**
     * GET /api/rooms/search?checkIn=2026-08-01&checkOut=2026-08-05&roomType=DELUXE
     * roomType is optional - omit it to search across all room types.
     */
    @GetMapping("/search")
    public List<Room> searchAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam(required = false) RoomType roomType) {
        return roomService.searchAvailableRooms(checkIn, checkOut, roomType);
    }

    @PatchMapping("/{id}/status")
    public Room updateStatus(@PathVariable Long id, @RequestParam RoomStatus status) {
        return roomService.updateRoomStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) {
        Room createdRoom = roomService.addRoom(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Room> createRoomWithId(@PathVariable Long id, @Valid @RequestBody Room room) {
        Room createdRoom = roomService.addRoom(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

}
