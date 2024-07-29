package com.cloudbees.grpc.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudbees.grpc.core.generated.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;


@GrpcService
public class BookingGrpcServerService extends BookingServiceGrpc.BookingServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(BookingGrpcServerService.class);

    private final TicketManagementSystem ticketManagementSystem = new TicketManagementSystem();
    private final SeatAllocator seatAllocator = new SeatAllocator();

    @Override
    public void createBooking(BookingRequest request, StreamObserver<BookingResponse> responseObserver) {
        try {
            String userEmail = request.getUser().getEmail();
            String firstName = request.getUser().getFirstName();
            String lastName = request.getUser().getLastName();
            
            // Actual Ticket booking logic
            String bookingId = ticketManagementSystem.bookTicket(userEmail, firstName, lastName);

            if (bookingId == null) {
                responseObserver.onError(new Exception("No booking found for user"));
                return;
            }
        
            TicketManagementSystem.Booking booking = ticketManagementSystem.viewBooking(bookingId);
            if (booking == null) {
                responseObserver.onError(new Exception("No booking found for booking ID"));
                return;
            }

            BookingResponse response = BookingResponse.newBuilder()
                    .setId(bookingId)
                    .setFrom("London")  // This may be updated if it was different in future
                    .setTo("France")    // This may be updated
                    .setPrice(20) // As this was fixed and handled same in Ticket Managment
                    .setSeat(ticketManagementSystem.viewBooking(bookingId).getSeat())
                    .setSection(ticketManagementSystem.viewBooking(bookingId).getSection())
                    .setUser(request.getUser())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new Exception("An error occurred while processing the booking: " + e.getMessage()));
        }
    }

    @Override
    public void getBookingByUser(GetBookingByUserRequest request, StreamObserver<BookingResponse> responseObserver) {
        try {
            String userEmail = request.getUser().getEmail();
            
            String bookingId = ticketManagementSystem.getUserBookings().get(userEmail);

            if (bookingId == null) {
                responseObserver.onError(new Exception("No booking found for user"));
                return;
            }

            TicketManagementSystem.Booking booking = ticketManagementSystem.viewBooking(bookingId);
            BookingResponse response = BookingResponse.newBuilder()
                    .setId(booking.getBookingId())
                    .setFrom("London")  // This may be updated if it was different in future
                    .setTo("France")    // This may be updated
                    .setPrice(20)       // This may be updated
                    .setSeat(booking.getSeat())
                    .setSection(booking.getSection())
                    .setUser(User.newBuilder()
                            .setFirstName(booking.getFirstName())    
                            .setLastName(booking.getLastName())    
                            .setEmail(userEmail)
                            .build())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getBookingsBySection(GetBookingsBySectionRequest request, StreamObserver<BookingListResponse> responseObserver) {
        try {
            BookingListResponse.Builder responseBuilder = BookingListResponse.newBuilder();

            for (TicketManagementSystem.Booking booking : ticketManagementSystem.getBookings().values()) {
                if (booking.getSection().equals(request.getSection())) {
                    BookingResponse response = BookingResponse.newBuilder()
                            .setId(booking.getBookingId())
                            .setFrom("London")  // This may be updated
                            .setTo("France")    // This may be updated
                            .setPrice(20)       // This may be updated
                            .setSeat(booking.getSeat())
                            .setSection(booking.getSection())
                            .setUser(User.newBuilder()
                                    .setFirstName(booking.getFirstName())    
                                    .setLastName(booking.getLastName()) 
                                    .setEmail(booking.getUserEmail())  // This may be updated
                                    .build())
                            .build();
                    responseBuilder.addBookings(response);
                }
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void removeBookingByUser(RemoveBookingByUserRequest request, StreamObserver<RemoveBookingResponse> responseObserver) {
        try {
            String userEmail = request.getUser().getEmail();
            String bookingId = ticketManagementSystem.getUserBookings().get(userEmail);

            if (bookingId == null) {
                responseObserver.onError(new Exception("No booking found for user"));
                return;
            }

            ticketManagementSystem.deleteBooking(bookingId);
            responseObserver.onNext(RemoveBookingResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void modifySeatByUser(SeatModificationRequest request, StreamObserver<SeatModificationResponse> responseObserver) {
        try {
            String userEmail = request.getUser().getEmail();
            String bookingId = ticketManagementSystem.getUserBookings().get(userEmail);

            String firstName = ticketManagementSystem.viewBooking(bookingId).getFirstName();
            String lastName = ticketManagementSystem.viewBooking(bookingId).getLastName();
            logger.info("bookingId: " + bookingId + " userEmail: " + userEmail + " firstName: " + firstName + " lastName: " + lastName
                        + " seat: " + request.getSeat() + " section: " + request.getSection());

            if (bookingId == null) {
                responseObserver.onError(new Exception("No booking found for user"));
                return;
            }

            TicketManagementSystem.Booking booking;
            if (seatAllocator.isSeatAvailable(request.getSeat(), request.getSection())) {
                booking = ticketManagementSystem.modifyBooking(bookingId, userEmail, request.getSeat(), request.getSection());
                logger.info("Seat modified successfully");
            }else {
                responseObserver.onError(new Exception("Request Seat Not available"));
                logger.error("Requested Seat Not available");
                return;
            }
            
            
            logger.info("Booking Id"+ booking.getBookingId() + "Seat: " + booking.getSeat() + "Section: " + booking.getSection());
            SeatModificationResponse response = SeatModificationResponse.newBuilder()
                    .setSection(request.getSection())
                    .setSeat(request.getSeat())
                    .setUser(User.newBuilder()
                            .setFirstName(firstName)
                            .setLastName(lastName)
                            .setEmail(userEmail)
                            .build())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
