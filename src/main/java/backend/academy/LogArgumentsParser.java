package backend.academy;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class LogArgumentsParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String path;
    private LocalDateTime from;
    private LocalDateTime to;
    private String format;

    public LogArgumentsParser(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        for (String arg : args) {
            String[] parts = arg.split("=");
            if (parts.length == 2) {
                arguments.put(parts[0], parts[1]);
            }
        }

        path = arguments.get("--path");
        format = arguments.get("--format");
        from = parseDate(arguments.get("--from"));
        to = parseDate(arguments.get("--to"));

        System.out.println("path: " + path);
    }

    private LocalDateTime parseDate(String dateStr) {
        return dateStr != null ? LocalDateTime.parse(dateStr, DATE_FORMATTER) : null;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public String getFormat() {
        return format;
    }
}
