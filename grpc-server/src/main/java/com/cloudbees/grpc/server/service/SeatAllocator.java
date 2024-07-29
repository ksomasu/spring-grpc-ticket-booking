package com.cloudbees.grpc.server.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SeatAllocator {
    private static final int MAX_SEATS_PER_SECTION = 100;
    private static final String[] SECTIONS = {"A", "B"};
    private final Map<Integer, Boolean> occupiedSeatsSectionA = new HashMap<>();
    private final Map<Integer, Boolean> occupiedSeatsSectionB = new HashMap<>();
    private final Random random = new Random();

    public SeatAllocator() {}

    public synchronized SeatAllocationResult allocateSeat() throws Exception {
        String section = findSection();
        int seat = random.nextInt(MAX_SEATS_PER_SECTION);

        if (section.equals("A")) {
            if (occupiedSeatsSectionA.containsKey(seat)) {
                return allocateSeat();
            }
            occupiedSeatsSectionA.put(seat, true);
        } else {
            if (occupiedSeatsSectionB.containsKey(seat)) {
                return allocateSeat();
            }
            occupiedSeatsSectionB.put(seat, true);
        }

        return new SeatAllocationResult(seat, section);
    }

    public synchronized boolean allocateSpecificSeat(int seat, String section) {
        if (!isSeatAvailable(seat, section)) {
            return false;
        }
        if (section.equals("A")) {
            occupiedSeatsSectionA.put(seat, true);
        } else {
            occupiedSeatsSectionB.put(seat, true);
        }
        return true;
    }

    public synchronized void deallocateSeat(int seat, String section) {
        if (section.equals("A")) {
            occupiedSeatsSectionA.remove(seat);
        } else {
            occupiedSeatsSectionB.remove(seat);
        }
    }

    public boolean isSeatAvailable(int seat, String section) {
        if (section.equals("A") && occupiedSeatsSectionA.size() < MAX_SEATS_PER_SECTION) {
            return !occupiedSeatsSectionA.containsKey(seat);
        } else if (section.equals("B") && occupiedSeatsSectionB.size() < MAX_SEATS_PER_SECTION) {
            return !occupiedSeatsSectionB.containsKey(seat);
        }
        return false;
    }

    private String findSection() throws Exception {
        if (occupiedSeatsSectionA.size() < MAX_SEATS_PER_SECTION && occupiedSeatsSectionB.size() < MAX_SEATS_PER_SECTION) {
            return SECTIONS[random.nextInt(2)];
        } else if (occupiedSeatsSectionA.size() < MAX_SEATS_PER_SECTION) {
            return "A";
        } else if (occupiedSeatsSectionB.size() < MAX_SEATS_PER_SECTION) {
            return "B";
        } else {
            throw new Exception("Max seats limit reached");
        }
    }

    public static class SeatAllocationResult {
        private final int seat;
        private final String section;

        public SeatAllocationResult(int seat, String section) {
            this.seat = seat;
            this.section = section;
        }

        public int getSeat() {
            return seat;
        }

        public String getSection() {
            return section;
        }
    }
}