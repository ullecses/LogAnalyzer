package parsers;

import backend.academy.models.LogRecord;
import backend.academy.parsers.LogParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LogParserLineTest {

    @Test
    void shouldParseValidLogLine() {
        // Arrange
        String validLogLine = "127.0.0.1 - user [12/Mar/2024:14:56:22 +0000] \"GET /index.html HTTP/1.1\" 200 512 \"-\" \"Mozilla/5.0\"";
        LogParser logParser = new LogParser();

        // Act
        LogRecord logRecord = logParser.parseLine(validLogLine);

        // Assert
        assertNotNull(logRecord, "Log record should not be null");
        assertEquals("127.0.0.1", logRecord.remoteAddress());
        assertEquals(200, logRecord.status());
    }

    @Test
    void shouldReturnNullForInvalidLogLine() {
        // Arrange
        String invalidLogLine = "This is not a valid log line";
        LogParser logParser = new LogParser();

        // Act
        LogRecord logRecord = logParser.parseLine(invalidLogLine);

        // Assert
        assertNull(logRecord, "Log record should be null for invalid line");
    }
}
