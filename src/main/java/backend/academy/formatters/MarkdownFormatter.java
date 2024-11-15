package backend.academy.formatters;

import backend.academy.LogReport;
import java.time.ZonedDateTime;

public class MarkdownFormatter implements ReportFormatter {

    @Override
    public String format(LogReport report, String path, ZonedDateTime from, ZonedDateTime to) {
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
            sb.append(String.format("| %d | %s | %d |\n", status, getHttpStatusName(status), count)
            ));

        return sb.toString();
    }
}
