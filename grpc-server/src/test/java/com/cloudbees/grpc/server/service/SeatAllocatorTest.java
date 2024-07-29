package com.cloudbees.grpc.server.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class SeatAllocatorTest {

    private SeatAllocator seatAllocator;

    @BeforeEach
    public void setUp() {
        seatAllocator = new SeatAllocator();
    }

    @Test
    public void testIsSeatAvailableTrue() throws Exception {
        int seatNumber = 1;
        String section = "A";

        Method method = SeatAllocator.class.getDeclaredMethod("isSeatAvailable", int.class, String.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(seatAllocator, seatNumber, section);

        assertTrue(result);
    }

    @Test
    public void testIsSeatAvailableFalse() throws Exception {
        int seatNumber = 1;
        String section = "A";

        seatAllocator.allocateSpecificSeat(seatNumber, section); 

        Method method = SeatAllocator.class.getDeclaredMethod("isSeatAvailable", int.class, String.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(seatAllocator, seatNumber, section);

        assertFalse(result);
    }
}