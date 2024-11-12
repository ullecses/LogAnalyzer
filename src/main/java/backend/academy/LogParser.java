package backend.academy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LogParser {

    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^(\\S+)\\s(\\S+)\\s\\S+\\s\\[(.+?)\\]\\s\"(.+?)\"\\s(\\d{3})\\s(\\d+|-)\\s\"(.*?)\"\\s\"(.*?)\"$"
    );

    DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);


    // Метод для парсинга строки лога
    public LogRecord parseLine(String logLine) {
        Matcher matcher = LOG_PATTERN.matcher(logLine);

        if (matcher.find()) {
            String remoteAddr = matcher.group(1);
            String remoteUser = matcher.group(2);
            String timeZonedStr = matcher.group(3);
            String request = matcher.group(4);
            int status = Integer.parseInt(matcher.group(5));
            int bodyBytesSent = matcher.group(6).equals("-") ? 0 : Integer.parseInt(matcher.group(6));
            String httpReferer = matcher.group(7);
            String httpUserAgent = matcher.group(8);

            try {
                // Используем ZonedDateTime для корректной обработки временной зоны
                ZonedDateTime timeZoned = ZonedDateTime.parse(timeZonedStr, TIME_FORMATTER);
                return new LogRecord(remoteAddr, remoteUser, timeZoned, request, status, bodyBytesSent,
                    httpReferer, httpUserAgent);
            } catch (DateTimeParseException e) {
                System.err.println("Ошибка при разборе даты: " + timeZonedStr);
                e.printStackTrace();
                return null;
            }
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
        URL obj = new URL(urlPath);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("GET Response Code :: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            return in.lines()
                .map(this::parseLine)
                .filter(record -> record != null);

        } else {
            System.out.println("GET request did not work.");
        }
        return null;
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
