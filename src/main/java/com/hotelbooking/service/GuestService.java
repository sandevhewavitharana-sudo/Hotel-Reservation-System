package com.hotelbooking.service;

import com.hotelbooking.model.Guest;

import java.util.List;

public interface GuestService {
    Guest registerGuest(Guest guest);
    Guest getGuestById(Long id);
    List<Guest> getAllGuests();
}
