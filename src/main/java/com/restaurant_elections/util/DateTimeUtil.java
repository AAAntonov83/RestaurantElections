package com.restaurant_elections.util;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import java.time.*;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class DateTimeUtil {
    private static Clock CLOCK = Clock.systemDefaultZone();

    public static void useMockTime(LocalDateTime dateTime) {
        Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        CLOCK = Clock.fixed(instant, ZoneId.systemDefault());
    }

    public static LocalDate currentDate() {
        return LocalDate.now(CLOCK);
    }

    public static LocalDateTime currentDateTime() {
        return LocalDateTime.now(CLOCK);
    }

    public static LocalDateTime atStartOfDay() {
        return currentDateTime().truncatedTo(ChronoUnit.DAYS);
    }

    public static LocalDateTime atStartOfDay(@NotNull LocalDateTime localDateTime) {
        return localDateTime.truncatedTo(ChronoUnit.DAYS);
    }

    public static LocalDateTime atStartOfNextDay(@NotNull LocalDateTime localDateTime) {
        return localDateTime.truncatedTo(ChronoUnit.DAYS).plusDays(1);
    }
}
