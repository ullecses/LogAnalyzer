package parsers;

import backend.academy.parsers.LogArgumentsParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LogArgumentsParserTest {

    @Test
    void shouldParseAllArgumentsCorrectly() {
        // Arrange
        String[] args = {
            "--path", "logs.log",
            "--format", "markdown",
            "--from", "2024-01-01",
            "--to", "2024-01-10",
            "--filter-field", "status",
            "--filter-value", "200"
        };

        // Act
        LogArgumentsParser parser = new LogArgumentsParser(args);

        // Assert
        assertEquals("logs.log", parser.path());
        assertEquals("markdown", parser.format());
        assertNotNull(parser.from());
        assertNotNull(parser.to());
        assertEquals("status", parser.filterField());
        assertEquals("200", parser.filterValue());
    }

    @Test
    void shouldHandleMissingArgumentsGracefully() {
        // Arrange
        String[] args = {"--path", "logs.log"};

        // Act
        LogArgumentsParser parser = new LogArgumentsParser(args);

        // Assert
        assertEquals("logs.log", parser.path());
        assertNull(parser.format());
        assertNull(parser.from());
        assertNull(parser.to());
    }
}
