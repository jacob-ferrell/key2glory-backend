package com.jacobferrell.Key2Glory.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateTime {
    private final LocalDateTime currentDateTime;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public DateTime() {
        this.currentDateTime = LocalDateTime.now();
    }
    public String toDateTimeString() {
        return dtf.format(currentDateTime);
    }

    public long toMillis() {
        // Convert LocalDateTime to milliseconds since epoch
        return currentDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}

