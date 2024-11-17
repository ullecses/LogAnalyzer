import backend.academy.analyzer.TotalLogInfo;
import backend.academy.formatters.AbstractReportFormatter;
import backend.academy.formatters.AsciiDocFormatter;
import backend.academy.formatters.MarkdownFormatter;
import backend.academy.utils.FileNameUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportFormatterTest {

    private TotalLogInfo mockReport;
    private AbstractReportFormatter asciiDocFormatter;
    private AbstractReportFormatter markdownFormatter;

    @BeforeEach
    void setUp() {
        mockReport = mock(TotalLogInfo.class);
        asciiDocFormatter = new AsciiDocFormatter();
        markdownFormatter = new MarkdownFormatter();
    }

    @Test
    void testGetHttpStatusName() {
        assertEquals("OK", asciiDocFormatter.getHttpStatusName(200));
        assertEquals("Not Found", markdownFormatter.getHttpStatusName(404));
        assertEquals("Unknown", markdownFormatter.getHttpStatusName(999));
    }

    @Test
    void testAsciiDocFormatHeader() {
        // Arrange
        String header = "Общая информация";

        // Act
        String result = asciiDocFormatter.formatHeader(header);

        // Assert
        assertEquals("== Общая информация\n\n", result);
    }

    @Test
    void testMarkdownFormatHeader() {
        // Arrange
        String header = "Общая информация";

        // Act
        String result = markdownFormatter.formatHeader(header);

        // Assert
        assertEquals("#### Общая информация\n\n", result);
    }

    @Test
    void testAsciiDocFormatTableStart() {
        // Arrange
        String tableHeader = "Метрика | Значение";

        // Act
        String result = asciiDocFormatter.formatTableStart(tableHeader);

        // Assert
        assertEquals("|===\n| Метрика | Значение \n", result);
    }

    @Test
    void testMarkdownFormatTableStart() {
        // Arrange
        String tableHeader = "Метрика | Значение";

        // Act
        String result = markdownFormatter.formatTableStart(tableHeader);

        // Assert
        assertEquals("| Метрика | Значение | \n|:-------:|:-------:|\n", result);
    }

    @Test
    void testAsciiDocFormatTableRow() {
        // Arrange
        String[] columns = {"Файл", "`log.txt`"};

        // Act
        String result = asciiDocFormatter.formatTableRow(columns);

        // Assert
        assertEquals("| Файл | `log.txt`\n", result);
    }

    @Test
    void testMarkdownFormatTableRow() {
        // Arrange
        String[] columns = {"Файл", "`log.txt`"};

        // Act
        String result = markdownFormatter.formatTableRow(columns);

        // Assert
        assertEquals("| Файл | `log.txt` |\n", result);
    }

    @Test
    void testAsciiDocFormatTableEnd() {
        // Act
        String result = asciiDocFormatter.formatTableEnd();

        // Assert
        assertEquals("|===\n\n", result);
    }

    @Test
    void testMarkdownFormatTableEnd() {
        // Act
        String result = markdownFormatter.formatTableEnd();

        // Assert
        assertEquals("\n", result);
    }

   @Test
    void testFormatReportInMarkdown() {
        // Arrange
        String path = "log.txt";
        ZonedDateTime from = ZonedDateTime.now();
        ZonedDateTime to = ZonedDateTime.now();
        mockReportData();

        // Act
        String report = markdownFormatter.format(mockReport, path, from, to);

        // Assert
        assertEquals(true, report.contains("#### Общая информация"));
        assertEquals(true, report.contains("| Файл(-ы) | `log.txt` |"));
    }

    private void mockReportData() {
        when(mockReport.totalRequests()).thenReturn(100);
        when(mockReport.getAverageBytesSent()).thenReturn(1024.0);
        when(mockReport.getUniqueIpCount()).thenReturn(10);
        when(mockReport.getMostPopularMethod()).thenReturn("GET");
        when(mockReport.getMostFrequentResources()).thenReturn(Map.of("/index", 50, "/home", 30));
        when(mockReport.getMostFrequentStatusCodes()).thenReturn(Map.of(200, 80, 404, 20));
    }
    @Test
    void shouldValidateFileFormat() {
        // Arrange
        String markdownFileName = FileNameUtil.getOutputFileName("markdown");
        String asciidocFileName = FileNameUtil.getOutputFileName("adoc");

        // Act & Assert
        assertEquals("report.md", markdownFileName, "Markdown file name should match");
        assertEquals("report.adoc", asciidocFileName, "AsciiDoc file name should match");
    }
}
