package com.hotelbooking.controller;

import com.hotelbooking.model.Payment;
import com.hotelbooking.model.PaymentMethod;
import com.hotelbooking.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/bookings/{bookingId}")
    public Payment pay(@PathVariable Long bookingId, @RequestParam PaymentMethod method) {
        return paymentService.processPayment(bookingId, method);
    }

    @GetMapping("/bookings/{bookingId}")
    public Payment getPayment(@PathVariable Long bookingId) {
        return paymentService.getPaymentForBooking(bookingId);
    }

    @PostMapping("/bookings/{bookingId}/refund")
    public Payment refund(@PathVariable Long bookingId) {
        return paymentService.refund(bookingId);
    }
}
