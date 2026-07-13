package com.hotelbooking.model;

/**
 * The catalog of room categories a hotel offers. Each type carries its
 * own base capacity so the booking logic can reject a party that's too
 * large for the room they picked, without hardcoding numbers elsewhere.
 */
public enum RoomType {
    SINGLE(1),
    DOUBLE(2),
    DELUXE(3),
    SUITE(5);

    private final int maxOccupancy;

    RoomType(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }

    public int getMaxOccupancy() {
        return maxOccupancy;
    }
}
