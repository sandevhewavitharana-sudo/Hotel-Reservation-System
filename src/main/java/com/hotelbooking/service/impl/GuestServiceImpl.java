package com.hotelbooking.service.impl;

import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.model.Guest;
import com.hotelbooking.repository.GuestRepository;
import com.hotelbooking.service.GuestService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;

    public GuestServiceImpl(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Override
    public Guest registerGuest(Guest guest) {
        if (guestRepository.existsByEmail(guest.getEmail())) {
            throw new IllegalArgumentException("A guest with email " + guest.getEmail() + " already exists");
        }
        return guestRepository.save(guest);
    }

    @Override
    public Guest getGuestById(Long id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found with id " + id));
    }

    @Override
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }
}
