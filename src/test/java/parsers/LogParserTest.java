package parsers;

import backend.academy.models.LogRecord;
import backend.academy.parsers.LogParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class LogParserTest {

    private LogParser logParser;

    @BeforeEach
    void setUp() {
        logParser = new LogParser();
    }

    @Test
    void testParseLine_ValidLogLine() {
        // Arrange
        String validLogLine = "192.168.0.1 - - [09/Apr/2023:14:21:45 +0000] \"GET /index.html HTTP/1.1\" 200 1234 \"http://example.com\" \"Mozilla/5.0\"";

        // Act
        LogRecord result = logParser.parseLine(validLogLine);

        // Assert
        assertNotNull(result);
        assertEquals("192.168.0.1", result.remoteAddress());
        assertEquals("GET /index.html HTTP/1.1", result.request());
        assertEquals(200, result.status());
        assertEquals(1234, result.bodyBytesSent());
        assertEquals("http://example.com", result.httpReferer());
        assertEquals("Mozilla/5.0", result.httpUserAgent());
        assertNotNull(result.timeZoned());
    }

    @Test
    void testParseLine_InvalidLogLine() {
        // Arrange
        String invalidLogLine = "INVALID LOG LINE FORMAT";

        // Act
        LogRecord result = logParser.parseLine(invalidLogLine);

        // Assert
        assertNull(result);
    }

    @Test
    void testLoadLogsFromFile() throws IOException {
        // Arrange
        Path tempFile = Files.createTempFile("logfile", ".log");
        Files.write(tempFile, List.of(
            "192.168.0.1 - - [09/Apr/2023:14:21:45 +0000] \"GET /index.html HTTP/1.1\" 200 1234 \"http://example.com\" \"Mozilla/5.0\"",
            "INVALID LOG LINE FORMAT"
        ));

        // Act
        Stream<LogRecord> logStream = logParser.loadLogFile(tempFile);

        // Assert
        List<LogRecord> logRecords = logStream.toList();
        assertEquals(1, logRecords.size());
        assertEquals("192.168.0.1", logRecords.get(0).remoteAddress());
    }

    @Test
    void testLoadLogsFromUrl() throws IOException {
        // Arrange
        String logContent = "192.168.0.1 - - [09/Apr/2023:14:21:45 +0000] \"GET /index.html HTTP/1.1\" 200 1234 \"http://example.com\" \"Mozilla/5.0\"";
        URL mockUrl = Mockito.mock(URL.class);
        HttpURLConnection mockConnection = Mockito.mock(HttpURLConnection.class);
        when(mockUrl.openConnection()).thenReturn(mockConnection);
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        InputStream inputStream = new java.io.ByteArrayInputStream(logContent.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        when(mockConnection.getInputStream()).thenReturn(inputStream);

        // Act
        try (Stream<String> lines = reader.lines()) {
            Stream<LogRecord> logStream = lines
                .map(logParser::parseLine)
                .filter(record -> record != null);

            // Assert
            List<LogRecord> logRecords = logStream.toList();
            assertEquals(1, logRecords.size());
            assertEquals("192.168.0.1", logRecords.get(0).remoteAddress());
        }
    }
}
