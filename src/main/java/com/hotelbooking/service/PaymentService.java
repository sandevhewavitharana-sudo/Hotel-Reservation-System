package com.hotelbooking.service;

import com.hotelbooking.model.Payment;
import com.hotelbooking.model.PaymentMethod;

public interface PaymentService {
    Payment processPayment(Long bookingId, PaymentMethod method);
    Payment getPaymentForBooking(Long bookingId);
    Payment refund(Long bookingId);
}
