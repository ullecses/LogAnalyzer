package backend.academy.formatters;

import backend.academy.LogReport;
import java.time.ZonedDateTime;
import java.util.Map;

public interface ReportFormatter {
    String format(LogReport report, String path, ZonedDateTime from, ZonedDateTime to);

    Map<Integer, String> HTTP_STATUS_NAMES = Map.of(
        200, "OK",
        206, "Partial Content",
        304, "Not Modified",
        403, "Forbidden",
        404, "Not Found",
        416, "Range Not Satisfiable",
        500, "Internal Server Error",
        502, "Bad Gateway",
        503, "Service Unavailable",
        504, "Gateway Timeout"
    );

    Map<String, String> REPORT_TEXT = Map.ofEntries(
        Map.entry("headerGeneralInfo", " Общая информация\n\n"),
        Map.entry("headerResources", " Запрашиваемые ресурсы\n\n"),
        Map.entry("headerStatusCodes", "#### Коды ответа\n\n"),
        Map.entry("tableMetrics", "| Метрика | Значение |\n|:-------:|---------:|\n"),
        Map.entry("tableResources", "| Ресурс | Количество |\n|:------:|-----------:|\n"),
        Map.entry("tableStatusCodes", "| Код | Имя | Количество |\n|:---:|:---:|-----------:|\n"),
        Map.entry("metricFile", "Файл(-ы)"),
        Map.entry("metricStartDate", "Начальная дата"),
        Map.entry("metricEndDate", "Конечная дата"),
        Map.entry("metricTotalRequests", "Количество запросов"),
        Map.entry("metricAvgResponseSize", "Средний размер ответа"),
        Map.entry("metric95Percentile", "95p размера ответа")
    );


    default String getHttpStatusName(int status) {
        return HTTP_STATUS_NAMES.getOrDefault(status, "Unknown");
    }

    default String getText(String key) {
        return REPORT_TEXT.getOrDefault(key, "");
    }
}
