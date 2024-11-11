package backend.academy;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.logging.Logger;

public class LogParser {

    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^(\\S+)\\s(\\S+)\\s\\[(.*?)\\]\\s\"(.*?)\"\\s(\\d{3})\\s(\\d+)\\s\"(.*?)\"\\s\"(.*?)\"$"
    );

    private static final Logger logger = Logger.getLogger(LogParser.class.getName());
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss");

    // Метод для парсинга строки лога
    public LogRecord parseLine(String logLine) {
        Matcher matcher = LOG_PATTERN.matcher(logLine);

        if (matcher.find()) {
            String remoteAddr = matcher.group(1);
            String remoteUser = matcher.group(2);
            LocalDateTime timeLocal = LocalDateTime.parse(matcher.group(3), TIME_FORMATTER);
            String request = matcher.group(4);
            int status = Integer.parseInt(matcher.group(5));
            int bodyBytesSent = Integer.parseInt(matcher.group(6));
            String httpReferer = matcher.group(7);
            String httpUserAgent = matcher.group(8);

            return new LogRecord(remoteAddr, remoteUser, timeLocal, request, status, bodyBytesSent, httpReferer, httpUserAgent);
        }
        return null;
    }

    // Загружаем логи как из файлов, так и из URL
    public Stream<LogRecord> loadLogs(String path) throws IOException {
        System.out.println("Received path: " + path); // Вывод для диагностики
        if (path.startsWith("http://") || path.startsWith("https://")) {
            // Если путь - это URL, загружаем логи через поток
            System.out.println("Detected URL path. Trying to load from URL...");
            return loadLogsFromUrl(path);  // Прямо загружаем данные из URL
        } else {
            // Если путь - это файл или директория, обрабатываем как локальный путь
            System.out.println("Detected local file/directory path. Trying to load from file system...");
            Path logPath = Paths.get(path);
            if (Files.isDirectory(logPath)) {
                return Files.list(logPath)
                    .filter(p -> p.toString().endsWith(".log"))
                    .flatMap(this::loadLogFile);
            } else {
                return loadLogFile(logPath);
            }
        }
    }

    // Метод для загрузки логов из URL
    private Stream<LogRecord> loadLogsFromUrl(String urlPath) throws IOException {
        System.out.println("Opening URL: " + urlPath); // Вывод для диагностики
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(urlPath).openStream()))) {
            return reader.lines()
                .map(this::parseLine)
                .filter(record -> record != null);
        }
    }

    // Метод для загрузки логов из файла
    private Stream<LogRecord> loadLogFile(Path filePath) {
        try {
            return Files.lines(filePath)
                .map(this::parseLine)
                .filter(record -> record != null);
        } catch (IOException e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }
}
