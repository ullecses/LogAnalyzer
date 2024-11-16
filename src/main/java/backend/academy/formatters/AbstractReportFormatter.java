package backend.academy.formatters;

import backend.academy.LogReport;
import java.time.ZonedDateTime;
import java.util.Map;

public abstract class AbstractReportFormatter {
    protected abstract String formatHeader(String header);

    protected abstract String formatTableStart(String tableHeader);

    protected abstract String formatTableRow(String... columns);

    protected abstract String formatTableEnd();

    static final Map<Integer, String> HTTP_STATUS_NAMES = Map.ofEntries(
        Map.entry(200, "OK"),
        Map.entry(201, "Created"),
        Map.entry(206, "Partial Content"),
        Map.entry(304, "Not Modified"),
        Map.entry(400, "Bad Request"),
        Map.entry(401, "Unauthorized"),
        Map.entry(403, "Forbidden"),
        Map.entry(404, "Not Found"),
        Map.entry(416, "Range Not Satisfiable"),
        Map.entry(500, "Internal Server Error"),
        Map.entry(502, "Bad Gateway"),
        Map.entry(503, "Service Unavailable"),
        Map.entry(504, "Gateway Timeout")
    );

    /*Map<Integer, String> HTTP_STATUS_NAMES = Map.of(
        200, "OK",
        201, "Created",
        400, "Bad Request",
        401, "Unauthorized",
        403, "Forbidden",
        404, "Not Found",
        500, "Internal Server Error",
        502, "Bad Gateway",
        503, "Service Unavailable"
    );*/

    public String getHttpStatusName(int status) {
        return HTTP_STATUS_NAMES.getOrDefault(status, "Unknown");
    }

    public String format(LogReport report, String path, ZonedDateTime from, ZonedDateTime to) {
        StringBuilder sb = new StringBuilder();

        // Общая информация
        sb.append(formatHeader("Общая информация"));
        sb.append(formatTableStart("Метрика | Значение"));
        sb.append(formatTableRow("Файл(-ы)", String.format("`%s`", path)));
        sb.append(formatTableRow("Начальная дата", from != null ? from.toString() : "-"));
        sb.append(formatTableRow("Конечная дата", to != null ? to.toString() : "-"));
        sb.append(formatTableRow("Количество запросов", String.valueOf(report.totalRequests())));
        sb.append(formatTableRow("Средний размер ответа", String.format("%.2fb", report.getAverageBytesSent())));
        sb.append(formatTableRow("95p размера ответа", String.format("%db", report.get95thPercentile())));
        sb.append(formatTableRow("Уникальных IP-адресов", String.valueOf(report.getUniqueIpCount())));
        sb.append(formatTableRow("Самый популярный метод", String.format("`%s`", report.getMostPopularMethod())));
        sb.append(formatTableEnd());

        // Запрашиваемые ресурсы
        sb.append(formatHeader("Запрашиваемые ресурсы"));
        sb.append(formatTableStart("Ресурс | Количество"));
        report.getMostFrequentResources().forEach((resource, count) ->
            sb.append(formatTableRow(resource, String.valueOf(count)))
        );
        sb.append(formatTableEnd());

        // Коды ответа
        sb.append(formatHeader("Коды ответа"));
        sb.append(formatTableStart("Код | Имя | Количество"));
        report.getMostFrequentStatusCodes().forEach((status, count) ->
            sb.append(formatTableRow(String.valueOf(status), getHttpStatusName(status), String.valueOf(count)))
        );
        sb.append(formatTableEnd());

        return sb.toString();
    }
}
