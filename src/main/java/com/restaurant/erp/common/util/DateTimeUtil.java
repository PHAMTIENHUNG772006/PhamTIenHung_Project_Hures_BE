package com.restaurant.erp.common.util;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public static String formatToString(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) return null;
        return zonedDateTime.format(ISO_FORMATTER);
    }

    public static ZonedDateTime parseToZonedDateTime(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        return ZonedDateTime.parse(dateStr, ISO_FORMATTER);
    }
}
