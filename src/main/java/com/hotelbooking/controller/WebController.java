package com.hotelbooking.controller;

import com.hotelbooking.dto.BookingRequest;
import com.hotelbooking.model.Room;
import com.hotelbooking.model.RoomType;
import com.hotelbooking.service.BookingService;
import com.hotelbooking.service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Serves the browsable demo UI (Thymeleaf server-rendered pages).
 * This is separate from the REST API in the other controllers - a
 * mobile app or another service would talk to /api/**, while a person
 * clicking around in a browser uses these routes.
 */
@Controller
public class WebController {

    private final RoomService roomService;
    private final BookingService bookingService;

    public WebController(RoomService roomService, BookingService bookingService) {
        this.roomService = roomService;
        this.bookingService = bookingService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam LocalDate checkIn,
                          @RequestParam LocalDate checkOut,
                          @RequestParam(required = false) RoomType roomType,
                          Model model) {
        List<Room> results = roomService.searchAvailableRooms(checkIn, checkOut, roomType);
        model.addAttribute("rooms", results);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("searched", true);
        return "index";
    }

    @GetMapping("/book/{roomId}")
    public String bookForm(@PathVariable Long roomId,
                            @RequestParam LocalDate checkIn,
                            @RequestParam LocalDate checkOut,
                            Model model) {
        model.addAttribute("room", roomService.getRoomById(roomId));
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        return "book";
    }

    @PostMapping("/book")
    public String submitBooking(@RequestParam Long roomId,
                                 @RequestParam Long guestId,
                                 @RequestParam LocalDate checkIn,
                                 @RequestParam LocalDate checkOut,
                                 @RequestParam int numberOfGuests,
                                 Model model) {
        BookingRequest request = new BookingRequest();
        request.setRoomId(roomId);
        request.setGuestId(guestId);
        request.setCheckInDate(checkIn);
        request.setCheckOutDate(checkOut);
        request.setNumberOfGuests(numberOfGuests);

        try {
            var booking = bookingService.createBooking(request);
            model.addAttribute("booking", booking);
            return "confirmation";
        } catch (RuntimeException ex) {
            model.addAttribute("room", roomService.getRoomById(roomId));
            model.addAttribute("checkIn", checkIn);
            model.addAttribute("checkOut", checkOut);
            model.addAttribute("error", ex.getMessage());
            return "book";
        }
    }

    @GetMapping("/bookings")
    public String allBookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "bookings";
    }

    @PostMapping("/bookings/{id}/confirm")
    public String confirm(@PathVariable Long id) {
        bookingService.confirmBooking(id);
        return "redirect:/bookings";
    }

    @PostMapping("/bookings/{id}/check-in")
    public String checkIn(@PathVariable Long id) {
        bookingService.checkIn(id);
        return "redirect:/bookings";
    }

    @PostMapping("/bookings/{id}/check-out")
    public String checkOut(@PathVariable Long id) {
        bookingService.checkOut(id);
        return "redirect:/bookings";
    }

    @PostMapping("/bookings/{id}/cancel")
    public String cancel(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return "redirect:/bookings";
    }
}
