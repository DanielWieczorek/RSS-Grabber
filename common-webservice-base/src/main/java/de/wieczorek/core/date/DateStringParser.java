package de.wieczorek.core.date;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

public final class DateStringParser {

    private static final char HOURS_UNIT = 'h';
    private static final char DAYS_UNIT = 'd';

    private DateStringParser() {
    }


    public static TemporalAmount parseDuration(String encodedDuration) {
        char unit = encodedDuration.charAt(encodedDuration.length() - 1);
        int amountEndIndex = encodedDuration.length() - 1;
        if (amountEndIndex <= 0) {
            throw new IllegalArgumentException("invalid amount end index: " + amountEndIndex);
        }

        int amount = Integer.decode(encodedDuration.substring(0, amountEndIndex));
        switch (unit) {
            case HOURS_UNIT:
                return Duration.ofHours(amount);
            case DAYS_UNIT:
                return Duration.ofDays(amount);
            default:
                throw new IllegalArgumentException("invalid unit: " + unit);
        }
    }
}
