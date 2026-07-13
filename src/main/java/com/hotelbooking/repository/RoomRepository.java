package com.hotelbooking.repository;

import com.hotelbooking.model.Room;
import com.hotelbooking.model.RoomStatus;
import com.hotelbooking.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByStatus(RoomStatus status);
    List<Room> findByRoomType(RoomType roomType);
    boolean existsByRoomNumber(String roomNumber);
}
