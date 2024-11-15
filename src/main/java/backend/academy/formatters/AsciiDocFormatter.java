package backend.academy.formatters;

import backend.academy.LogReport;
import java.time.ZonedDateTime;

public class AsciiDocFormatter implements ReportFormatter {

    @Override
    public String format(LogReport report, String path, ZonedDateTime from, ZonedDateTime to) {
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
}
