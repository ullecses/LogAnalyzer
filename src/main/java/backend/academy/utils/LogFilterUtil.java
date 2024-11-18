package backend.academy.utils;

import backend.academy.models.LogRecord;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LogFilterUtil {

    private LogFilterUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Stream<LogRecord> filterByDateRange(Stream<LogRecord> records, ZonedDateTime from, ZonedDateTime to) {
        return records.filter(logRecord -> {
            ZonedDateTime time = logRecord.timeZoned();
            boolean afterFrom = (from == null || time.isAfter(from) || time.isEqual(from));
            boolean beforeTo = (to == null || time.isBefore(to) || time.isEqual(to));
            return afterFrom && beforeTo;
        });
    }

    // Сопоставление полей и функций для их получения как строки
    private static final Map<String, Function<LogRecord, String>> FIELD_EXTRACTORS = new HashMap<>();

    static {
        FIELD_EXTRACTORS.put("remoteAddress", LogRecord::remoteAddress);
        FIELD_EXTRACTORS.put("remoteUser", LogRecord::remoteUser);
        FIELD_EXTRACTORS.put("timeZonedStr", LogRecord::timeZonedStr);
        FIELD_EXTRACTORS.put("request", LogRecord::request);
        FIELD_EXTRACTORS.put("status", log -> Integer.toString(log.status()));
        FIELD_EXTRACTORS.put("bodyBytesSent", log -> Integer.toString(log.bodyBytesSent()));
        FIELD_EXTRACTORS.put("httpReferer", LogRecord::httpReferer);
        FIELD_EXTRACTORS.put("httpUserAgent", LogRecord::httpUserAgent);
    }

    // Фильтр по полю и значению
    public static Stream<LogRecord> filterByField(Stream<LogRecord> logs, String field, String value) {
        Predicate<LogRecord> filterPredicate = getFieldPredicate(field, value);
        return logs.filter(filterPredicate);
    }

    // Создает предикат для фильтрации логов по заданному полю и значению
    private static Predicate<LogRecord> getFieldPredicate(String field, String value) {
        Pattern pattern = Pattern.compile(value.replace("*", ".*")); // Поддержка подстановочных знаков
        Function<LogRecord, String> extractor = FIELD_EXTRACTORS.get(field);

        return logRecord -> {
            if (extractor == null) {
                return false; // Поле не найдено
            }
            String fieldValue = extractor.apply(logRecord);
            return fieldValue != null && pattern.matcher(fieldValue).matches();
        };
    }
}
