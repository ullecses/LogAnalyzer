package backend.academy.parsers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter public class LogArgumentsParser {

    private static final DateTimeFormatter DATE_FORMATTER_LOCAL = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final String path;
    private final ZonedDateTime from;
    private final ZonedDateTime to;
    private final String format;
    private final String filterField;
    private final String filterValue;

    public LogArgumentsParser(String[] args) {
        Map<String, String> arguments = parseArguments(args);
        path = arguments.get("--path");
        format = arguments.get("--format");
        from = parseDate(arguments.get("--from"));
        to = parseDate(arguments.get("--to"));
        filterField = arguments.get("--filter-field");
        filterValue = arguments.get("--filter-value");
    }

    private Map<String, String> parseArguments(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            if (i + 1 < args.length) {
                arguments.put(args[i], args[i + 1]);
            }
        }
        return arguments;
    }

    private ZonedDateTime parseDate(String dateStr) {
        if (dateStr == null) {
            return null;
        }

        // Парсинг строки как LocalDate
        LocalDate localDate = LocalDate.parse(dateStr, DATE_FORMATTER_LOCAL);

        // Преобразование LocalDate в ZonedDateTime с временной зоной по умолчанию
        return localDate.atStartOfDay(ZoneId.systemDefault());
    }
}
