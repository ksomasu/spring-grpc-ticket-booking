package com.cloudbees.grpc.server.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TicketManagementSystem {
    private final Map<String, String> userBookings = new HashMap<>(); // Maps user email to booking ID
    private final Map<String, Booking> bookings = new HashMap<>(); // Maps booking ID to Booking

    private final SeatAllocator seatAllocator = new SeatAllocator();

    public synchronized String bookTicket(String userEmail, String firstName, String lastName) throws Exception {
        if (userBookings.containsKey(userEmail)) {
            throw new Exception("User has already booked a ticket");
        }

        SeatAllocator.SeatAllocationResult seatAllocationResult = seatAllocator.allocateSeat();
        String bookingId = UUID.randomUUID().toString();
        Booking booking = new Booking(bookingId, userEmail, seatAllocationResult.getSeat(), seatAllocationResult.getSection(), firstName, lastName);

        userBookings.put(userEmail, bookingId);
        bookings.put(bookingId, booking);

        return bookingId;
    }

    public synchronized Booking viewBooking(String bookingId) {
        return bookings.get(bookingId);
    }

    public synchronized void deleteBooking(String bookingId) throws Exception {
        Booking booking = bookings.remove(bookingId);
        if (booking == null) {
            throw new Exception("Booking not found");
        }

        userBookings.remove(booking.getUserEmail());
        seatAllocator.deallocateSeat(booking.getSeat(), booking.getSection());
    }

    public synchronized Booking modifyBooking(String bookingId, String userEmail, int seat, String section) throws Exception {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new Exception("Booking not found");
        }

        if (!booking.getUserEmail().equals(userEmail)) {
            throw new Exception("User does not have permission to modify this booking");
        }
        String newBookingId = UUID.randomUUID().toString();
        Booking newBooking = new Booking(newBookingId, userEmail, seat, section, booking.getFirstName(), booking.getLastName());
        bookings.remove(bookingId);
        bookings.put(newBookingId, newBooking);
        userBookings.remove(userEmail);
        userBookings.put(userEmail, newBookingId);

        return newBooking;
    }

    // Add this getter method for bookings
    public Map<String, Booking> getBookings() {
        return bookings;
    }

    // Add this getter method for userBookings
    public Map<String, String> getUserBookings() {
        return userBookings;
    }

    public static class Booking {
        private final String bookingId;
        private final String userEmail;
        private final int seat;
        private final String section;
        private final String firstName;
        private final String lastName;

        public Booking(String bookingId, String userEmail, int seat, String section, String firstName, String lastName) {
            this.bookingId = bookingId;
            this.userEmail = userEmail;
            this.seat = seat;
            this.section = section;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getBookingId() {
            return bookingId;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public int getSeat() {
            return seat;
        }

        public String getSection() {
            return section;
        }

        public String getFirstName() {
            return firstName;
        }
    
        public String getLastName() {
            return lastName;
        }
    }
}