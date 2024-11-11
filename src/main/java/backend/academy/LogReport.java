package backend.academy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LogReport {

    private int totalRequests; // Общее количество запросов
    private Map<String, Integer> resourceFrequency; // Частота запросов к ресурсам
    private Map<Integer, Integer> statusFrequency; // Частота кодов ответа
    private long totalBytesSent; // Общий размер ответа
    private int logCount; // Количество записей для среднего размера ответа
    private List<Integer> responseSizes = new ArrayList<>();

    public LogReport() {
        this.totalRequests = 0;
        this.resourceFrequency = new HashMap<>();
        this.statusFrequency = new HashMap<>();
        this.totalBytesSent = 0;
        this.logCount = 0;
    }

    // Метод для добавления записи лога
    public void addLogRecord(LogRecord record) {
        totalRequests++;
        logCount++;

        // Добавляем в список для перцентиля
        responseSizes.add(record.getBodyBytesSent());

        // Увеличиваем счетчик для ресурса
        String resource = record.getRequest().split(" ")[1];
        resourceFrequency.put(resource, resourceFrequency.getOrDefault(resource, 0) + 1);

        // Увеличиваем счетчик для кода ответа
        int status = record.getStatus();
        statusFrequency.put(status, statusFrequency.getOrDefault(status, 0) + 1);

        // Увеличиваем общий объем ответов
        totalBytesSent += record.getBodyBytesSent();
    }

    // Метод для получения среднего размера ответа
    public double getAverageBytesSent() {
        return logCount > 0 ? (double) totalBytesSent / logCount : 0;
    }

    // Получить общее количество запросов
    public int getTotalRequests() {
        return totalRequests;
    }

    // Получить наиболее часто запрашиваемые ресурсы
    public Map<String, Integer> getMostFrequentResources() {
        return resourceFrequency;
    }

    // Получить наиболее частые коды ответа
    public Map<Integer, Integer> getMostFrequentStatusCodes() {
        return statusFrequency;
    }

    // Метод для вычисления 95-го перцентиля
    public long get95thPercentile() {
        if (responseSizes.isEmpty()) {
            return 0;
        }

        // Сортируем и находим 95-й перцентиль
        List<Integer> sortedSizes = responseSizes.stream()
            .sorted()
            .collect(Collectors.toList());
        int index = (int) Math.ceil(0.95 * sortedSizes.size()) - 1;
        return sortedSizes.get(index);
    }
}
