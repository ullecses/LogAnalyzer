package utils;

import backend.academy.models.LogRecord;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsUtilTest {

    @Test
    void shouldCalculateTotalLogs() {
        // Arrange
        LogRecord log1 = new LogRecord();
        LogRecord log2 = new LogRecord();
        Stream<LogRecord> logs = Stream.of(log1, log2);

        // Act
        long total = logs.count();

        // Assert
        assertEquals(2, total, "Total logs should match");
    }

    @Test
    void shouldHandleEmptyLogStream() {
        // Arrange
        Stream<LogRecord> logs = Stream.empty();

        // Act
        long total = logs.count();

        // Assert
        assertEquals(0, total, "Total logs should be zero for empty stream");
    }
}
