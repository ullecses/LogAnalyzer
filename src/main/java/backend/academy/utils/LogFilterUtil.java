package backend.academy.utils;

import backend.academy.models.LogRecord;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

public class LogFilterUtil {

    private LogFilterUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Stream<LogRecord> filterByDateRange(Stream<LogRecord> records, ZonedDateTime from, ZonedDateTime to) {
        return records.filter(logRecord -> {
            ZonedDateTime time = logRecord.timeZoned();
            boolean afterFrom = (from == null || time.isAfter(from) || time.isEqual(from));
            boolean beforeTo = (to == null || time.isBefore(to) || time.isEqual(to));
            return afterFrom && beforeTo;
        });
    }
}
