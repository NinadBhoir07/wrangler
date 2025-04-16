package io.cdap.wrangler.api.parser;

import io.cdap.wrangler.api.annotations.PublicEvolving;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Token class for representing byte size values with units (e.g., 10KB, 1.5MB)
 */
@PublicEvolving
public class ByteSize extends Token {
    private static final Pattern BYTE_PATTERN = 
        Pattern.compile("^(\\d+(?:\\.\\d+)?)\\s*([KMGTP]?i?B)$", Pattern.CASE_INSENSITIVE);
    private final long bytes;

    /**
     * Constructs a ByteSize token from string representation
     * @param value String value like "10KB" or "1.5MiB"
     * @throws IllegalArgumentException if format is invalid
     */
    public ByteSize(String value) {
        super(TokenType.BYTE_SIZE, value);
        Matcher matcher = BYTE_PATTERN.matcher(value.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException(
                String.format("Invalid byte size format '%s'. Expected format like '10KB', '1.5MB'", value));
        }

        double size = Double.parseDouble(matcher.group(1));
        String unit = matcher.group(2).toUpperCase();

        switch (unit) {
            case "B":
                bytes = (long) size;
                break;
            case "KB":
                bytes = (long) (size * 1000L);
                break;
            case "MB":
                bytes = (long) (size * 1000L * 1000L);
                break;
            case "GB":
                bytes = (long) (size * 1000L * 1000L * 1000L);
                break;
            case "TB":
                bytes = (long) (size * 1000L * 1000L * 1000L * 1000L);
                break;
            case "PB":
                bytes = (long) (size * 1000L * 1000L * 1000L * 1000L * 1000L);
                break;
            case "KIB":
                bytes = (long) (size * 1024L);
                break;
            case "MIB":
                bytes = (long) (size * 1024L * 1024L);
                break;
            case "GIB":
                bytes = (long) (size * 1024L * 1024L * 1024L);
                break;
            case "TIB":
                bytes = (long) (size * 1024L * 1024L * 1024L * 1024L);
                break;
            case "PIB":
                bytes = (long) (size * 1024L * 1024L * 1024L * 1024L * 1024L);
                break;
            default:
                throw new IllegalArgumentException("Unknown byte size unit: " + unit);
        }
    }

    /**
     * @return the size in bytes (canonical unit)
     */
    public long getBytes() {
        return bytes;
    }

    /**
     * @return the original string representation
     */
    @Override
    public String value() {
        return (String) super.value();
    }
}