package com.cloudbees.grpc.client.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.cloudbees.grpc.core.generated.*;


import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
public class TicketBookingGrpcClientService {

    private static final Logger logger = LoggerFactory.getLogger(TicketBookingGrpcClientService.class);

    @GrpcClient("grpc-ticket-booking-client")
    private BookingServiceGrpc.BookingServiceBlockingStub bookingServiceBlockingStub;

    public SeatModificationResponse modifySeatByUser(SeatModificationRequest request) {
        try {
            return bookingServiceBlockingStub.modifySeatByUser(request);
        } catch (StatusRuntimeException e) {
            logger.error("gRPC call failed with status: " + e.getStatus().toString(), e);
            throw e; // Re-throw the exception if needed
        } catch (Exception e) {
            logger.error("Unexpected error occurred", e);
            throw e; // Re-throw the exception if needed
        }
    }

    public void createBooking(String email, String from, String to, int price) {

        User userObj = User.newBuilder()
                .setFirstName("Karth")
                .setLastName("som") 
                .setEmail(email)
                .build();

        BookingRequest bookingRequest = BookingRequest.newBuilder()
                .setUser(userObj)
                .setFrom(from)
                .setTo(to)
                .setPrice(price)
                .build();
        BookingResponse bookingResponse = this.bookingServiceBlockingStub.createBooking(bookingRequest);

        Assert.notNull(bookingResponse, "Booking response is null!");
        logger.info(String.format("Received booking response from the server: %s", bookingResponse.getId()));
        logger.info("Booking created successfully");
    }

    public void getBookingByUser(String user) {
        User userObj = User.newBuilder()
                .setFirstName(user) // Assuming user is the first name
                .build();

        GetBookingByUserRequest request = GetBookingByUserRequest.newBuilder().setUser(userObj).build();
        BookingResponse response = this.bookingServiceBlockingStub.getBookingByUser(request);

        Assert.notNull(response, "Booking response is null!");
        logger.info(String.format("Received booking response from the server: %s", response.getId()));
        logger.info("Booking retrieved successfully");
    }

    public void getBookingsBySection(String section) {
        GetBookingsBySectionRequest request = GetBookingsBySectionRequest.newBuilder().setSection(section).build();
        BookingListResponse response = this.bookingServiceBlockingStub.getBookingsBySection(request);

        Assert.notNull(response, "Booking list response is null!");
        
        response.getBookingsList().forEach(booking -> {
            logger.info(String.format("Booking ID: %s, From: %s, To: %s, Price: %d, Seat: %d, Section: %s, User: %s",
                    booking.getId(), booking.getFrom(), booking.getTo(), booking.getPrice(), booking.getSeat(), booking.getSection(), booking.getUser().getEmail()));
        });
        
        logger.info(String.format("Received %d bookings from the server for section %s", response.getBookingsCount(), section));
        logger.info("Bookings retrieved successfully");
    }

    public void removeBookingByUser(String user) {
        User userObj = User.newBuilder()
                .setFirstName(user) // Assuming user is the first name
                .build();

        RemoveBookingByUserRequest request = RemoveBookingByUserRequest.newBuilder().setUser(userObj).build();
        RemoveBookingResponse response = this.bookingServiceBlockingStub.removeBookingByUser(request);

        //Assert.isTrue(response.getSuccess(), "Failed to remove booking!");
        logger.info(String.format("Booking for user %s removed successfully", user));
    }

    public void modifySeatByUser(String user, int seat, String section) {
        User userObj = User.newBuilder()
                .setFirstName(user) // Assuming user is the first name
                .build();

        SeatModificationRequest request = SeatModificationRequest.newBuilder()
                .setUser(userObj)
                .setSeat(seat)
                .setSection(section)
                .build();
                try {
                    SeatModificationResponse response = this.bookingServiceBlockingStub.modifySeatByUser(request);
                    Assert.isTrue(response.getSeat() != seat, "Failed to modify seat!");
                    logger.info(String.format("Seat for user %s modified successfully to seat %d in section %s", user, seat, section));
                } catch (StatusRuntimeException e) {
                    // Log detailed error information
                    logger.error("gRPC call failed with status: " + e.getStatus().toString(), e);
                } catch (Exception e) {
                    // Log unexpected exceptions
                    logger.error("Unexpected error occurred", e);
                }    
            }
}