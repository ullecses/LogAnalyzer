package utils;

import backend.academy.models.LogRecord;
import backend.academy.utils.LogFilterUtil;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LogFilterUtilTest {

    @Test
    void shouldFilterLogsByDateRange() {
        // Arrange
        Stream<LogRecord> logs = Stream.of(
            createLogRecord("2024-01-01T10:00:00Z"),
            createLogRecord("2024-01-05T10:00:00Z"),
            createLogRecord("2024-01-10T10:00:00Z")
        );
        ZonedDateTime from = ZonedDateTime.parse("2024-01-02T00:00:00Z");
        ZonedDateTime to = ZonedDateTime.parse("2024-01-09T23:59:59Z");

        // Act
        Stream<LogRecord> filteredLogs = LogFilterUtil.filterByDateRange(logs, from, to);

        // Assert
        assertEquals(1, filteredLogs.count());
    }

    @Test
    void shouldFilterLogsWithinDateRange() {
        // Arrange
        ZonedDateTime from = ZonedDateTime.parse("2024-01-01T00:00:00Z");
        ZonedDateTime to = ZonedDateTime.parse("2024-12-31T23:59:59Z");

        LogRecord log1 = new LogRecord().timeZoned(ZonedDateTime.parse("2024-03-15T12:00:00Z"));
        LogRecord log2 = new LogRecord().timeZoned(ZonedDateTime.parse("2023-12-31T23:59:59Z"));
        Stream<LogRecord> logs = Stream.of(log1, log2);

        // Act
        List<LogRecord> filteredLogs = LogFilterUtil.filterByDateRange(logs, from, to).toList();

        // Assert
        assertEquals(1, filteredLogs.size(), "Only one log should be within range");
        assertEquals(log1, filteredLogs.get(0));
    }

    @Test
    void shouldHandleNullRange() {
        // Arrange
        LogRecord log = new LogRecord().timeZoned(ZonedDateTime.parse("2024-03-15T12:00:00Z"));
        Stream<LogRecord> logs = Stream.of(log);

        // Act
        List<LogRecord> filteredLogs = LogFilterUtil.filterByDateRange(logs, null, null).toList();

        // Assert
        assertEquals(1, filteredLogs.size(), "All logs should pass when range is null");
    }

    private LogRecord createLogRecord(String dateTime) {
        LogRecord logRecord = new LogRecord();
        logRecord.timeZoned(ZonedDateTime.parse(dateTime));
        return logRecord;
    }
}

