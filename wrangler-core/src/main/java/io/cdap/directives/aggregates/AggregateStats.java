package io.cdap.wrangler.directives.aggregate;

import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.parser.*;
import io.cdap.wrangler.executor.Context;

import java.util.List;

@PublicEvolving
public class AggregateStats implements Directive, Aggregate {
    public static final String NAME = "aggregate-stats";
    private String sizeColumn;
    private String durationColumn;
    private String totalSizeColumn;
    private String totalDurationColumn;
    private String sizeOutputUnit;
    private String durationOutputUnit;
    private boolean calculateAverage;

    private long totalBytes = 0L;
    private long totalNanoseconds = 0L;
    private int rowCount = 0;

    @Override
    public UsageDefinition define() {
        UsageDefinition.Builder builder = UsageDefinition.builder(NAME);
        builder.define("size-column", TokenType.COLUMN_NAME);
        builder.define("duration-column", TokenType.COLUMN_NAME);
        builder.define("total-size-column", TokenType.COLUMN_NAME);
        builder.define("total-duration-column", TokenType.COLUMN_NAME);
        builder.define("size-output-unit", TokenType.TEXT, true);
        builder.define("duration-output-unit", TokenType.TEXT, true);
        builder.define("calculate-average", TokenType.BOOLEAN, true);
        return builder.build();
    }

    @Override
    public void initialize(Arguments args) throws DirectiveParseException {
        this.sizeColumn = ((ColumnName) args.value("size-column")).value();
        this.durationColumn = ((ColumnName) args.value("duration-column")).value();
        this.totalSizeColumn = ((ColumnName) args.value("total-size-column")).value();
        this.totalDurationColumn = ((ColumnName) args.value("total-duration-column")).value();
        this.sizeOutputUnit = args.contains("size-output-unit") ? 
            ((Text) args.value("size-output-unit")).value() : "B";
        this.durationOutputUnit = args.contains("duration-output-unit") ? 
            ((Text) args.value("duration-output-unit")).value() : "ms";
        this.calculateAverage = args.contains("calculate-average") && 
            ((Bool) args.value("calculate-average")).value();
    }

    @Override
    public void destroy() {
        // No resources to clean up
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
        for (Row row : rows) {
            try {
                Object sizeValue = row.getValue(sizeColumn);
                if (sizeValue != null) {
                    ByteSize byteSize = new ByteSize(sizeValue.toString());
                    totalBytes += byteSize.getBytes();
                }

                Object durationValue = row.getValue(durationColumn);
                if (durationValue != null) {
                    TimeDuration duration = new TimeDuration(durationValue.toString());
                    totalNanoseconds += duration.getNanoseconds();
                }
                rowCount++;
            } catch (Exception e) {
                throw new DirectiveExecutionException(
                    NAME, String.format("Error processing row %s: %s", row, e.getMessage()), e);
            }
        }
        return rows;
    }

    @Override
    public List<Row> finalize() throws DirectiveExecutionException {
        Row result = new Row();
        
        double convertedSize = convertBytes(totalBytes, sizeOutputUnit);
        result.add(totalSizeColumn, convertedSize);
        
        double totalDuration = calculateAverage ? 
            (double) totalNanoseconds / rowCount : totalNanoseconds;
        double convertedDuration = convertNanoseconds(totalDuration, durationOutputUnit);
        result.add(totalDurationColumn, convertedDuration);
        
        return Collections.singletonList(result);
    }

    private double convertBytes(long bytes, String unit) {
        switch (unit.toUpperCase()) {
            case "KB": return bytes / 1000.0;
            case "MB": return bytes / (1000.0 * 1000.0);
            case "GB": return bytes / (1000.0 * 1000.0 * 1000.0);
            case "TB": return bytes / (1000.0 * 1000.0 * 1000.0 * 1000.0);
            case "PB": return bytes / (1000.0 * 1000.0 * 1000.0 * 1000.0 * 1000.0);
            case "KIB": return bytes / 1024.0;
            case "MIB": return bytes / (1024.0 * 1024.0);
            case "GIB": return bytes / (1024.0 * 1024.0 * 1024.0);
            case "TIB": return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0);
            case "PIB": return bytes / (1024.0 * 1024.0 * 1024.0 * 1024.0 * 1024.0);
            default: return bytes; // Default to bytes
        }
    }

    private double convertNanoseconds(double ns, String unit) {
        switch (unit.toLowerCase()) {
            case "ns": return ns;
            case "us": case "µs": return ns / 1000.0;
            case "ms": return ns / (1000.0 * 1000.0);
            case "s": return ns / (1000.0 * 1000.0 * 1000.0);
            case "m": return ns / (1000.0 * 1000.0 * 1000.0 * 60.0);
            case "h": return ns / (1000.0 * 1000.0 * 1000.0 * 60.0 * 60.0);
            case "d": return ns / (1000.0 * 1000.0 * 1000.0 * 60.0 * 60.0 * 24.0);
            default: return ns / (1000.0 * 1000.0); // Default to milliseconds
        }
    }
}