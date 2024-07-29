package com.cloudbees.grpc.server.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cloudbees.grpc.core.generated.SeatModificationRequest;
import com.cloudbees.grpc.core.generated.SeatModificationResponse;
import com.cloudbees.grpc.core.generated.User;

import io.grpc.stub.StreamObserver;

@ExtendWith(MockitoExtension.class)
public class BookingGrpcServerServiceTest {

    @Mock
    private TicketManagementSystem ticketManagementSystem;

    @Mock
    private SeatAllocator seatAllocator;

    @Mock
    private StreamObserver<SeatModificationResponse> responseObserver;

    @InjectMocks
    private BookingGrpcServerService bookingGrpcServerService;

    private SeatModificationRequest request;

    @BeforeEach
    public void setUp() {
        request = SeatModificationRequest.newBuilder()
                .setUser(User.newBuilder().setEmail("test@example.com").build())
                .setSeat(1)
                .setSection("A")
                .build();
    }

    @Test
    public void testModifySeatSuccess() throws Exception {
        when(seatAllocator.allocateSpecificSeat(anyInt(), anyString())).thenReturn(true);

        bookingGrpcServerService.modifySeatByUser(request, responseObserver);

        verify(responseObserver).onNext(any(SeatModificationResponse.class));
        verify(responseObserver).onCompleted();
    }

    @Test
    public void testModifySeatAllocationFailed() throws Exception {
        when(seatAllocator.allocateSpecificSeat(anyInt(), anyString())).thenReturn(false);

        bookingGrpcServerService.modifySeatByUser(request, responseObserver);

        verify(responseObserver).onError(any(Exception.class));
    }

    @Test
    public void testModifySeatException() throws Exception {
        doThrow(new Exception("Test Exception")).when(seatAllocator).allocateSpecificSeat(anyInt(), anyString());

        bookingGrpcServerService.modifySeatByUser(request, responseObserver);

        verify(responseObserver).onError(any(Exception.class));
    }
}