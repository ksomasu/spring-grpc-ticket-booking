package com.cloudbees.grpc.server.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TicketManagementSystemTest {

    @Mock
    private SeatAllocator seatAllocator;

    @InjectMocks
    private TicketManagementSystem ticketManagementSystem;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testBookTicketSuccess() throws Exception {
        String userEmail = "user@example.com";
        SeatAllocator.SeatAllocationResult seatAllocationResult = new SeatAllocator.SeatAllocationResult(1, "A");
        when(seatAllocator.allocateSeat()).thenReturn(seatAllocationResult);

        String bookingId = ticketManagementSystem.bookTicket(userEmail, "first", "last");

        assertNotNull(bookingId);
        assertEquals(1, ticketManagementSystem.getBookings().size());
        assertEquals(1, ticketManagementSystem.getUserBookings().size());
    }

    @Test
    public void testBookTicketUserAlreadyBooked() throws Exception {
        String userEmail = "user@example.com";
        SeatAllocator.SeatAllocationResult seatAllocationResult = new SeatAllocator.SeatAllocationResult(1, "A");
        when(seatAllocator.allocateSeat()).thenReturn(seatAllocationResult);

        ticketManagementSystem.bookTicket(userEmail, "first", "last");
        assertThrows(Exception.class, () -> {
            ticketManagementSystem.bookTicket(userEmail, "first", "last");
        });
    }

    @Test
    public void testViewBookingSuccess() throws Exception {
        String userEmail = "user@example.com";
        SeatAllocator.SeatAllocationResult seatAllocationResult = new SeatAllocator.SeatAllocationResult(1, "A");
        when(seatAllocator.allocateSeat()).thenReturn(seatAllocationResult);

        String bookingId = ticketManagementSystem.bookTicket(userEmail, "first", "last");
        TicketManagementSystem.Booking booking = ticketManagementSystem.viewBooking(bookingId);

        assertNotNull(booking);
        assertEquals(bookingId, booking.getBookingId());
        assertEquals(userEmail, booking.getUserEmail());
    }

    @Test
    public void testViewBookingNotFound() {
        TicketManagementSystem.Booking booking = ticketManagementSystem.viewBooking("non-existing-id");

        assertNull(booking);
    }

    @Test
    public void testDeleteBookingSuccess() throws Exception {
        String userEmail = "user@example.com";
        SeatAllocator.SeatAllocationResult seatAllocationResult = new SeatAllocator.SeatAllocationResult(1, "A");
        when(seatAllocator.allocateSeat()).thenReturn(seatAllocationResult);

        String bookingId = ticketManagementSystem.bookTicket(userEmail, "first", "last");
        ticketManagementSystem.deleteBooking(bookingId);

        assertEquals(0, ticketManagementSystem.getBookings().size());
        assertEquals(0, ticketManagementSystem.getUserBookings().size());
        verify(seatAllocator).deallocateSeat(1, "A");
    }

    @Test
    public void testDeleteBookingNotFound() {
        assertThrows(Exception.class, () -> {
            ticketManagementSystem.deleteBooking("non-existing-id");
        });
    }
}
