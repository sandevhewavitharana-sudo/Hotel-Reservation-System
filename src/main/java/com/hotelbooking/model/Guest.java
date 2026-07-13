package com.hotelbooking.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * A hotel guest who can make bookings. Extends {@link Person}, so it
 * inherits name/email/phone and only needs to define what makes a
 * guest a guest: loyalty points and the list of bookings they've made.
 */
@Entity
@Table(name = "guests")
public class Guest extends Person {

    private int loyaltyPoints;

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Booking> bookings = new ArrayList<>();

    public Guest() {
        super();
    }

    public Guest(String firstName, String lastName, String email, String phone) {
        super(firstName, lastName, email, phone);
        this.loyaltyPoints = 0;
    }

    @Override
    public String getRoleLabel() {
        return "GUEST";
    }

    /** Called whenever a stay is completed, so repeat customers accumulate rewards. */
    public void earnLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }
}
