package backend.academy.analyzer;

import backend.academy.formatters.AbstractReportFormatter;
import backend.academy.formatters.AsciiDocFormatter;
import backend.academy.formatters.MarkdownFormatter;
import backend.academy.io.OutputHandler;
import backend.academy.models.LogRecord;
import backend.academy.parsers.LogArgumentsParser;
import backend.academy.parsers.LogParser;
import backend.academy.utils.FileNameUtil;
import backend.academy.utils.LogFilterUtil;
import java.io.IOException;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

public class LogAnalyzer {

    private static final FileNameUtil.FileFormat DEFAULT_FORMAT = FileNameUtil.FileFormat.MARKDOWN;
    PrintStream printStream;
    LogArgumentsParser parser;
    OutputHandler outputHandler;

    public LogAnalyzer(PrintStream printStream, LogArgumentsParser parser) {
        this.printStream = printStream;
        this.parser = parser;
        this.outputHandler = new OutputHandler(printStream);
    }

    public void analyze() throws IOException {
        String path = parser.path();
        ZonedDateTime from = parser.from();
        ZonedDateTime to = parser.to();

        // Используем enum для определения формата
        FileNameUtil.FileFormat format = getFormatOrDefault(parser.format());

        LogParser logParser = new LogParser();
        Stream<LogRecord> logStream = logParser.loadLogs(path);

        // Фильтрация по временным параметрам
        logStream = LogFilterUtil.filterByDateRange(logStream, from, to);

        TotalLogInfo report = new TotalLogInfo();
        logStream.forEach(report::addLogRecord);

        // Выбор нужного форматтера
        AbstractReportFormatter formatter = switch (format) {
            case MARKDOWN -> new MarkdownFormatter();
            case ASCIIDOC -> new AsciiDocFormatter();
            default -> throw new IllegalArgumentException("Unsupported format: " + format.formatName());
        };

        // Генерация отчета с выбранным форматтером
        String formattedReport = formatter.format(report, path, from, to);
        outputHandler.printLine(formattedReport);
    }

    // Метод для получения формата или значения по умолчанию
    private FileNameUtil.FileFormat getFormatOrDefault(String format) {
        if (format == null) {
            return DEFAULT_FORMAT;
        }
        try {
            return FileNameUtil.getOutputFileFormat(format);
        } catch (IllegalArgumentException e) {
            return DEFAULT_FORMAT; // Если формат неизвестен, возвращаем формат по умолчанию
        }
    }
}
