package utils;

import backend.academy.models.LogRecord;
import backend.academy.utils.LogFilterUtil;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FilterByFieldTest {

    private List<LogRecord> logs;

    @BeforeEach
    void setUp() {
        // Arrange
        LogRecord log1 = new LogRecord();
        log1.remoteAddress("192.168.0.1");
        log1.remoteUser("user1");
        log1.request("GET /home HTTP/1.1");
        log1.status(200);
        log1.httpUserAgent("Mozilla/5.0");

        LogRecord log2 = new LogRecord();
        log2.remoteAddress("192.168.0.2");
        log2.remoteUser("user2");
        log2.request("POST /login HTTP/1.1");
        log2.status(403);
        log2.httpUserAgent("Mozilla/4.0");

        LogRecord log3 = new LogRecord();
        log3.remoteAddress("192.168.0.3");
        log3.remoteUser("user3");
        log3.request("GET /dashboard HTTP/1.1");
        log3.status(500);
        log3.httpUserAgent("Chrome/91.0");

        logs = List.of(log1, log2, log3);
    }

    @Test
    void testFilterByField_RemoteAddress() {
        // Arrange: подготавливаем входные данные
        String field = "remoteAddress";
        String value = "192.168.0.*";

        // Act: фильтруем логи по IP-адресу
        Stream<LogRecord> filteredLogs = LogFilterUtil.filterByField(logs.stream(), field, value);

        // Assert: проверяем, что количество совпадающих записей соответствует ожиданиям
        assertEquals(3, filteredLogs.count(), "Фильтрация по IP-адресу не прошла");
    }

    @Test
    void testFilterByField_RemoteUser() {
        // Arrange
        String field = "remoteUser";
        String value = "user2";

        // Act
        Stream<LogRecord> filteredLogs = LogFilterUtil.filterByField(logs.stream(), field, value);

        // Assert
        assertEquals(1, filteredLogs.count(), "Фильтрация по имени пользователя не прошла");
    }

    @Test
    void testFilterByField_Request() {
        // Arrange
        String field = "request";
        String value = "GET /home*";

        // Act
        Stream<LogRecord> filteredLogs = LogFilterUtil.filterByField(logs.stream(), field, value);

        // Assert
        assertEquals(1, filteredLogs.count(), "Фильтрация по запросу не прошла");
    }

    @Test
    void testFilterByField_Status() {
        // Arrange
        String field = "status";
        String value = "403";

        // Act
        Stream<LogRecord> filteredLogs = LogFilterUtil.filterByField(logs.stream(), field, value);

        // Assert
        assertEquals(1, filteredLogs.count(), "Фильтрация по статусу не прошла");
    }

    @Test
    void testFilterByField_HttpUserAgent() {
        // Arrange
        String field = "httpUserAgent";
        String value = "Mozilla*";

        // Act
        Stream<LogRecord> filteredLogs = LogFilterUtil.filterByField(logs.stream(), field, value);

        // Assert
        assertEquals(2, filteredLogs.count(), "Фильтрация по User-Agent не прошла");
    }
}

