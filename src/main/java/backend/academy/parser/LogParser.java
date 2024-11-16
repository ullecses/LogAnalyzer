package backend.academy.parser;

import backend.academy.LogRecord;
import lombok.Getter;
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

public class LogParser {

    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^(\\S+)\\s(\\S+)\\s\\S+\\s\\[(.+?)\\]\\s\"(.+?)\"\\s(\\d{3})\\s(\\d+|-)\\s\"(.*?)\"\\s\"(.*?)\"$"
    );

    static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

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
            String remoteAddr = matcher.group(LogGroup.REMOTE_ADDRESS.index());
            String remoteUser = matcher.group(LogGroup.REMOTE_USER.index());
            String timeZonedStr = matcher.group(LogGroup.TIME_ZONED.index());
            String request = matcher.group(LogGroup.REQUEST.index());
            int status = Integer.parseInt(matcher.group(LogGroup.STATUS.index()));
            int bodyBytesSent = matcher.group(LogGroup.BODY_BYTES_SENT.index()).equals("-") ? 0
                : Integer.parseInt(matcher.group(LogGroup.BODY_BYTES_SENT.index()));
            String httpReferer = matcher.group(LogGroup.HTTP_REFERER.index());
            String httpUserAgent = matcher.group(LogGroup.HTTP_USER_AGENT.index());

            try {
                // Используем ZonedDateTime для корректной обработки временной зоны
                ZonedDateTime timeZoned = ZonedDateTime.parse(timeZonedStr, TIME_FORMATTER);
                return new LogRecord(remoteAddr, remoteUser, timeZoned, request, status, bodyBytesSent,
                    httpReferer, httpUserAgent);
            } catch (DateTimeParseException e) {
                System.err.println("Ошибка при разборе даты: " + timeZonedStr);
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

    private List<Path> findFiles(String wildcardPath) throws IOException {
        Path baseDir = Paths.get("").toAbsolutePath();
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + wildcardPath);

        try (Stream<Path> stream = Files.walk(baseDir)) {
            return stream.filter(Files::isRegularFile)
                .filter(matcher::matches)
                .toList();
        }
    }

    // Метод для загрузки логов из URL
    private Stream<LogRecord> loadLogsFromUrl(String urlPath) throws IOException {
        URL obj = new URL(urlPath);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            return in.lines()
                .map(this::parseLine)
                .filter(logRecord -> logRecord != null);
        }
        return null;
    }

    // Метод для загрузки логов из файла
    private Stream<LogRecord> loadLogFile(Path file) {
        try {
            return Files.lines(file)
                .map(this::parseLine) // Парсим каждую строку
                .filter(logRecord -> logRecord != null); // Фильтруем валидные записи
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла " + file + ": " + e.getMessage());
            return Stream.empty();
        }
    }
}
