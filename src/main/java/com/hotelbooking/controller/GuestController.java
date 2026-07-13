package com.hotelbooking.controller;

import com.hotelbooking.dto.GuestRequest;
import com.hotelbooking.model.Guest;
import com.hotelbooking.service.GuestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @PostMapping
    public ResponseEntity<Guest> register(@Valid @RequestBody GuestRequest request) {
        Guest guest = new Guest(request.getFirstName(), request.getLastName(), request.getEmail(), request.getPhone());
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.registerGuest(guest));
    }

    @GetMapping
    public List<Guest> getAllGuests() {
        return guestService.getAllGuests();
    }

    @GetMapping("/{id}")
    public Guest getGuest(@PathVariable Long id) {
        return guestService.getGuestById(id);
    }
}
