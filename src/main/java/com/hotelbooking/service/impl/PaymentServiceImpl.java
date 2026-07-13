package com.hotelbooking.service.impl;

import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.model.Booking;
import com.hotelbooking.model.BookingStatus;
import com.hotelbooking.model.Payment;
import com.hotelbooking.model.PaymentMethod;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.PaymentRepository;
import com.hotelbooking.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public Payment processPayment(Long bookingId, PaymentMethod method) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot pay for a cancelled booking");
        }

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseGet(() -> new Payment(booking, method));

        payment.setMethod(method);
        payment.markPaid();
        return paymentRepository.save(payment);
    }

    @Override
    public Payment getPaymentForBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("No payment found for booking " + bookingId));
    }

    @Override
    public Payment refund(Long bookingId) {
        Payment payment = getPaymentForBooking(bookingId);
        payment.markRefunded();
        return paymentRepository.save(payment);
    }
}
