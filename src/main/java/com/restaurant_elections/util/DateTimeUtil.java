package com.restaurant_elections.util;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class DateTimeUtil {

    public static LocalDateTime atStartOfDay() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
    }

    public static LocalDateTime atStartOfDay(@NotNull LocalDateTime localDateTime) {
        return localDateTime.truncatedTo(ChronoUnit.DAYS);
    }
}
