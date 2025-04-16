package io.cdap.wrangler.api.parser;

import io.cdap.wrangler.api.annotations.PublicEvolving;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Token class for representing time durations with units (e.g., 100ms, 1.5s)
 */
@PublicEvolving
public class TimeDuration extends Token {
    private static final Pattern DURATION_PATTERN = 
        Pattern.compile("^(\\d+(?:\\.\\d+)?)\\s*(ns|us|µs|ms|s|m|h|d)$");
    private final long nanoseconds;

    /**
     * Constructs a TimeDuration token from string representation
     * @param value String value like "100ms" or "1.5h"
     * @throws IllegalArgumentException if format is invalid
     */
    public TimeDuration(String value) {
        super(TokenType.TIME_DURATION, value);
        Matcher matcher = DURATION_PATTERN.matcher(value.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                String.format("Invalid time duration format '%s'. Expected format like '100ms', '1.5s'", value));
        }

        double duration = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2);

        switch (unit) {
            case "ns":
                nanoseconds = (long) duration;
                break;
            case "us":
            case "µs":
                nanoseconds = (long) (duration * 1000L);
                break;
            case "ms":
                nanoseconds = (long) (duration * 1000L * 1000L);
                break;
            case "s":
                nanoseconds = (long) (duration * 1000L * 1000L * 1000L);
                break;
            case "m":
                nanoseconds = (long) (duration * 1000L * 1000L * 1000L * 60L);
                break;
            case "h":
                nanoseconds = (long) (duration * 1000L * 1000L * 1000L * 60L * 60L);
                break;
            case "d":
                nanoseconds = (long) (duration * 1000L * 1000L * 1000L * 60L * 60L * 24L);
                break;
            default:
                throw new IllegalArgumentException("Unknown time duration unit: " + unit);
        }
    }

    /**
     * @return the duration in nanoseconds (canonical unit)
     */
    public long getNanoseconds() {
        return nanoseconds;
    }

    /**
     * @return the original string representation
     */
    @Override
    public String value() {
        return (String) super.value();
    }
}