package backend.academy;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

public class LogFilter {

    public static Stream<LogRecord> filterByDateRange(Stream<LogRecord> records, ZonedDateTime from, ZonedDateTime to) {
        return records.filter(record -> {
            ZonedDateTime time = record.timeZoned();
            boolean afterFrom = (from == null || time.isAfter(from) || time.isEqual(from));
            boolean beforeTo = (to == null || time.isBefore(to) || time.isEqual(to));
            return afterFrom && beforeTo;
        });
    }
}
