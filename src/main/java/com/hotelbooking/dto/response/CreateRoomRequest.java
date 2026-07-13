package com.hotelbooking.dto.response;

import com.hotelbooking.model.RoomType;
import jakarta.validation.constraints.*;

public class CreateRoomRequest {

    @NotBlank(message = "Room number is required")
    @Size(max = 10, message = "Room number must not exceed 10 characters")
    private String roomNumber;

    @NotNull(message = "Room type is required")
    private RoomType roomType;

    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "1.00",message = "Price per night must be 1.00")
    @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
    private Double pricePerNight;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 10, message = "Capacity must not exceed 10")
    private Integer capacity;

    @NotNull(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;


    public String getRoomNumber() {
    }

    public RoomType getRoomType() {
    }
}
