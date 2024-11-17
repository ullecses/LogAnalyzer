package backend.academy.parsers;

import backend.academy.models.LogRecord;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.Getter;

public class LogParser {

    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^(\\S+)\\s(\\S+)\\s\\S+\\s\\[(.+?)\\]\\s\"(.+?)\"\\s(\\d{3})\\s(\\d+|-)\\s\"(.*?)\"\\s\"(.*?)\"$"
    );

    static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z",
        Locale.ENGLISH);

    // Метод для парсинга строки лога
    public LogRecord parseLine(String logLine) {
        Matcher matcher = LOG_PATTERN.matcher(logLine);

        @Getter enum LogGroup {
            REMOTE_ADDRESS(1),
            REMOTE_USER(2),
            TIME_ZONED(3),
            REQUEST(4),
            STATUS(5),
            BODY_BYTES_SENT(6),
            HTTP_REFERER(7),
            HTTP_USER_AGENT(8);

            private final int index;

            LogGroup(int index) {
                this.index = index;
            }
        }

        if (matcher.find()) {
            LogRecord logRecord = new LogRecord();
            logRecord.remoteAddress(matcher.group(LogGroup.REMOTE_ADDRESS.index()));
            logRecord.remoteUser(matcher.group(LogGroup.REMOTE_USER.index()));
            logRecord.timeZonedStr(matcher.group(LogGroup.TIME_ZONED.index()));
            logRecord.request(matcher.group(LogGroup.REQUEST.index()));
            logRecord.status(Integer.parseInt(matcher.group(LogGroup.STATUS.index())));
            logRecord.bodyBytesSent(matcher.group(LogGroup.BODY_BYTES_SENT.index()).equals("-") ? 0
                : Integer.parseInt(matcher.group(LogGroup.BODY_BYTES_SENT.index())));
            logRecord.httpReferer(matcher.group(LogGroup.HTTP_REFERER.index()));
            logRecord.httpUserAgent(matcher.group(LogGroup.HTTP_USER_AGENT.index()));

            try {
                // Используем ZonedDateTime для корректной обработки временной зоны
                logRecord.timeZoned(ZonedDateTime.parse(logRecord.timeZonedStr(), TIME_FORMATTER));
                return logRecord;
            } catch (DateTimeParseException e) {
                System.err.println("Ошибка при разборе даты: " + logRecord.timeZonedStr());
                return null;
            }
        }
        return null;
    }

    // Загружаем логи как из файлов, так и из URL
    public Stream<LogRecord> loadLogs(String path) throws IOException {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            // Если путь - это URL, загружаем логи через поток
            return loadLogsFromUrl(path);
        } else {
            // Если путь - это файл или директория, обрабатываем как локальный путь
            List<Path> files = findFiles(path);
            return files.stream()
                .flatMap(this::loadLogFile);
        }
    }

    public List<Path> findFiles(String wildcardPath) throws IOException {
        Path baseDir = Paths.get("").toAbsolutePath();
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + wildcardPath);

        try (Stream<Path> stream = Files.walk(baseDir)) {
            return stream.filter(Files::isRegularFile)
                .filter(matcher::matches)
                .toList();
        }
    }

    // Метод для загрузки логов из URL
    public Stream<LogRecord> loadLogsFromUrl(String urlPath) throws IOException {
        URL obj = new URL(urlPath);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            return in.lines()
                .map(this::parseLine)
                .filter(logRecord -> logRecord != null);
        }
        return null;
    }

    // Метод для загрузки логов из файла
    public Stream<LogRecord> loadLogFile(Path file) {
        try {
            return Files.lines(file)
                .map(this::parseLine) // Парсим каждую строку
                .filter(logRecord -> logRecord != null);
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла " + file + ": " + e.getMessage());
            return Stream.empty();
        }
    }
}
