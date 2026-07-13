package com.hotelbooking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.MappedSuperclass;

/**
 * Common attributes shared by anyone the system needs to identify as a
 * human being (currently just Guest, but Staff could extend this later
 * without duplicating name/email/phone handling).
 *
 * @MappedSuperclass tells JPA "this class isn't its own table, but push
 * its fields down into whichever subclass entity extends it" - this is
 * how inheritance is represented in a relational database.
 */
@MappedSuperclass
public abstract class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    protected Person() {
        // required by JPA
    }

    protected Person(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    /** Every concrete person type must be able to describe itself for logs/receipts. */
    public abstract String getRoleLabel();

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // --- getters / setters (encapsulation: fields stay private) ---

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
