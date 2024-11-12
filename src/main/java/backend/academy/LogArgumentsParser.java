package backend.academy;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter public class LogArgumentsParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String path;
    private ZonedDateTime from;
    private ZonedDateTime to;
    private String format;

    public LogArgumentsParser(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            arguments.put(args[i], args[i + 1]);
        }

        path = arguments.get("--path");
        format = arguments.get("--format");
        from = parseDate(arguments.get("--from"));
        to = parseDate(arguments.get("--to"));
    }

    private ZonedDateTime parseDate(String dateStr) {
        return dateStr != null ? ZonedDateTime.parse(dateStr, DATE_FORMATTER) : null;
    }

}
