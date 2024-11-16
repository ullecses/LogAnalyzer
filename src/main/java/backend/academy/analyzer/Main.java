package backend.academy.analyzer;

import backend.academy.parser.LogArgumentsParser;
import backend.academy.LogFilterUtil;
import backend.academy.parser.LogParser;
import backend.academy.LogRecord;
import backend.academy.LogReport;
import backend.academy.OutputHandler;
import backend.academy.formatters.AbstractReportFormatter;
import backend.academy.formatters.AsciiDocFormatter;
import backend.academy.formatters.MarkdownFormatter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {

    public static void main(String[] args) throws IOException {
        PrintStream printStream = null;
        final String MARKDOWN_CONST = "markdown";
        try {
            // Создание потока для записи в файл
            printStream = new PrintStream(new FileOutputStream("report.md"));

            // Использование outputHandler с printStream
            OutputHandler outputHandler = new OutputHandler(printStream);

            LogArgumentsParser parser = new LogArgumentsParser(args);

            String path = parser.path();
            ZonedDateTime from = parser.from();
            ZonedDateTime to = parser.to();
            String format = parser.format() != null ? parser.format() : MARKDOWN_CONST;

            LogParser logParser = new LogParser();
            Stream<LogRecord> logStream = logParser.loadLogs(path);

            // Фильтрация по временным параметрам
            logStream = LogFilterUtil.filterByDateRange(logStream, from, to);

            LogReport report = new LogReport();
            logStream.forEach(report::addLogRecord);

            // Выбор нужного форматтера
            AbstractReportFormatter formatter;
            if (MARKDOWN_CONST.equalsIgnoreCase(format)) {
                formatter = new MarkdownFormatter();
            } else if ("adoc".equalsIgnoreCase(format)) {
                formatter = new AsciiDocFormatter();
            } else {
                throw new IllegalArgumentException("Unsupported format: " + format);
            }

            // Генерация отчета с выбранным форматтером
            String formattedReport = formatter.format(report, path, from, to);
            outputHandler.printLine(formattedReport);
            printStream.flush();

        } catch (FileNotFoundException e) {
            System.err.println("Ошибка при создании файла: " + e.getMessage());
        } finally {
            if (printStream != null) {
                printStream.close();
            }
        }
    }
}
