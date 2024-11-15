package backend.academy.analyzer;

import backend.academy.LogArgumentsParser;
import backend.academy.LogFilter;
import backend.academy.LogParser;
import backend.academy.LogRecord;
import backend.academy.LogReport;
import backend.academy.OutputHandler;
import backend.academy.formatters.AsciiDocFormatter;
import backend.academy.formatters.MarkdownFormatter;
import backend.academy.formatters.ReportFormatter;
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
        try {
            // Создание потока для записи в файл
            printStream = new PrintStream(new FileOutputStream("report.md"));
            //printStream = System.out;

            // Использование outputHandler с printStream
            OutputHandler outputHandler = new OutputHandler(printStream);

            LogArgumentsParser parser = new LogArgumentsParser(args);

            String path = parser.path();
            ZonedDateTime from = parser.from();
            ZonedDateTime to = parser.to();
            String format = parser.format() != null ? parser.format() : "markdown";

            LogParser logParser = new LogParser();
            Stream<LogRecord> logStream = logParser.loadLogs(path);

            // Фильтрация по временным параметрам
            logStream = LogFilter.filterByDateRange(logStream, from, to);

            LogReport report = new LogReport();
            logStream.forEach(report::addLogRecord);

            // Выбор нужного форматтера
            ReportFormatter formatter;
            if ("markdown".equalsIgnoreCase(format)) {
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
                System.out.printf(" закрытие потока");
                // Закрываем поток после записи
                printStream.close();
            }
        }
    }
}
