package backend.academy;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public class LogFilter {

    public static Stream<LogRecord> filterByDateRange(Stream<LogRecord> records, LocalDateTime from, LocalDateTime to) {
        return records.filter(record -> {
            LocalDateTime time = record.getTimeLocal();
            boolean afterFrom = (from == null || time.isAfter(from) || time.isEqual(from));
            boolean beforeTo = (to == null || time.isBefore(to) || time.isEqual(to));
            return afterFrom && beforeTo;
        });
    }
}
