package backend.academy.analyzer;

import backend.academy.LogArgumentsParser;
import backend.academy.LogFilter;
import backend.academy.LogParser;
import backend.academy.LogRecord;
import backend.academy.LogReport;
import backend.academy.OutputHandler;
import java.io.IOException;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {

    public static void main(String[] args) throws IOException {
        PrintStream printStream = System.out;
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

        // Генерация отчета
        if ("markdown".equalsIgnoreCase(format)) {
            outputHandler.printLine(generateMarkdownReport(report, path, from, to));
        } else if ("adoc".equalsIgnoreCase(format)) {
            outputHandler.printLine(generateAsciiDocReport(report, path, from, to));
        }
    }

    private static String generateMarkdownReport(LogReport report, String path, ZonedDateTime from, ZonedDateTime to) {
        StringBuilder sb = new StringBuilder();
        sb.append("#### Общая информация\n\n");
        sb.append("| Метрика | Значение |\n|:-------:|---------:|\n");
        sb.append(String.format("| Файл(-ы) | `%s` |\n", path));
        sb.append(String.format("| Начальная дата | %s |\n", from != null ? from : "-"));
        sb.append(String.format("| Конечная дата | %s |\n", to != null ? to : "-"));
        sb.append(String.format("| Количество запросов | %d |\n", report.totalRequests()));
        sb.append(String.format("| Средний размер ответа | %.2fb |\n", report.getAverageBytesSent()));
        sb.append(String.format("| 95p размера ответа | %db |\n", report.get95thPercentile()));

        sb.append("\n#### Запрашиваемые ресурсы\n\n");
        sb.append("| Ресурс | Количество |\n|:------:|-----------:|\n");
        report.getMostFrequentResources().forEach((resource, count) ->
            sb.append(String.format("| %s | %d |\n", resource, count))
        );

        sb.append("\n#### Коды ответа\n\n");
        sb.append("| Код | Имя | Количество |\n|:---:|:---:|-----------:|\n");
        report.getMostFrequentStatusCodes().forEach((status, count) ->
            sb.append(String.format("| %d | %s | %d |\n", status, getHttpStatusName(status), count))
        );

        return sb.toString();
    }

    private static String generateAsciiDocReport(LogReport report, String path, ZonedDateTime from, ZonedDateTime to) {
        StringBuilder sb = new StringBuilder();
        sb.append("== Общая информация\n\n");
        sb.append("|===\n| Метрика | Значение\n");
        sb.append(String.format("| Файл(-ы) | `%s`\n", path));
        sb.append(String.format("| Начальная дата | %s\n", from != null ? from : "-"));
        sb.append(String.format("| Конечная дата | %s\n", to != null ? to : "-"));
        sb.append(String.format("| Количество запросов | %d\n", report.totalRequests()));
        sb.append(String.format("| Средний размер ответа | %.2fb\n", report.getAverageBytesSent()));
        sb.append(String.format("| 95p размера ответа | %db\n", report.get95thPercentile()));
        sb.append("|===\n\n");

        sb.append("== Запрашиваемые ресурсы\n\n");
        sb.append("|===\n| Ресурс | Количество\n");
        report.getMostFrequentResources().forEach((resource, count) ->
            sb.append(String.format("| %s | %d\n", resource, count))
        );
        sb.append("|===\n\n");

        sb.append("== Коды ответа\n\n");
        sb.append("|===\n| Код | Имя | Количество\n");
        report.getMostFrequentStatusCodes().forEach((status, count) ->
            sb.append(String.format("| %d | %s | %d\n", status, getHttpStatusName(status), count))
        );
        sb.append("|===\n");

        return sb.toString();
    }

    private static String getHttpStatusName(int status) {
        switch (status) {
            case 200: return "OK";
            case 206: return "Partial Content";
            case 304: return "Not Modified";
            case 403: return "Forbidden";
            case 404: return "Not Found";
            case 416: return "Range Not Satisfiable";
            case 500: return "Internal Server Error";
            case 502: return "Bad Gateway";
            case 503: return "Service Unavailable";
            case 504: return "Gateway Timeout";
            default: return "Unknown";
        }
    }
}
