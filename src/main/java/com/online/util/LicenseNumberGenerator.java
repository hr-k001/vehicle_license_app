package com.online.util;

import java.time.Year;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// US-015: Generate Unique License Number — Format: DL-YYYY-000001
public class LicenseNumberGenerator {

    private final ConcurrentHashMap<Integer, AtomicLong> yearCounters = new ConcurrentHashMap<>();

    public synchronized String generateLicenseNumber() {
        int currentYear = Year.now().getValue();
        AtomicLong counter = yearCounters.computeIfAbsent(currentYear, k -> new AtomicLong(0));
        long seq = counter.incrementAndGet();
        if (seq > 999999) throw new IllegalStateException("License number sequence limit exceeded for year " + currentYear);
        return String.format("DL-%d-%06d", currentYear, seq);
    }

    public void resetCounterForYear(int year) {
        yearCounters.put(year, new AtomicLong(0));
    }

    public long getCurrentSequenceForYear(int year) {
        return yearCounters.getOrDefault(year, new AtomicLong(0)).get();
    }
}
