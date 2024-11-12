package backend.academy;

import java.time.ZonedDateTime;

public interface ReportFormatter {
    String format(LogReport report, String path, ZonedDateTime from, ZonedDateTime to);
}
