package backend.academy.analyzer;

import backend.academy.models.LogRecord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

public class TotalLogInfo {

    private static final double PERCENTILE_95 = 0.95;

    // Получить общее количество запросов
    @Getter private int totalRequests; // Общее количество запросов
    private Map<String, Integer> resourceFrequency; // Частота запросов к ресурсам
    private Map<Integer, Integer> statusFrequency; // Частота кодов ответа
    private Map<String, Integer> methodFrequency; // Частота HTTP-методов
    private Set<String> uniqueIps; // Уникальные IP-адреса
    private long totalBytesSent; // Общий размер ответа
    private int logCount; // Количество записей для среднего размера ответа
    private List<Integer> responseSizes = new ArrayList<>();

    public TotalLogInfo() {
        this.totalRequests = 0;
        this.resourceFrequency = new HashMap<>();
        this.statusFrequency = new HashMap<>();
        this.methodFrequency = new HashMap<>();
        this.uniqueIps = new HashSet<>();
        this.totalBytesSent = 0;
        this.logCount = 0;
    }

    // Метод для добавления записи лога
    public void addLogRecord(LogRecord logRecord) {
        totalRequests++;
        logCount++;

        // Добавляем в список для перцентиля
        responseSizes.add(logRecord.bodyBytesSent());

        // Увеличиваем счетчик для ресурса
        String resource = logRecord.request().split(" ")[1];
        resourceFrequency.put(resource, resourceFrequency.getOrDefault(resource, 0) + 1);

        // Увеличиваем счетчик для кода ответа
        int status = logRecord.status();
        statusFrequency.put(status, statusFrequency.getOrDefault(status, 0) + 1);

        // Увеличиваем общий объем ответов
        totalBytesSent += logRecord.bodyBytesSent();

        // Добавляем уникальный IP-адрес
        uniqueIps.add(logRecord.remoteAddress());

        // Увеличиваем счётчик для HTTP-метода
        String method = logRecord.request().split(" ")[0];
        methodFrequency.put(method, methodFrequency.getOrDefault(method, 0) + 1);
    }

    // Метод для получения среднего размера ответа
    public double getAverageBytesSent() {
        return logCount > 0 ? (double) totalBytesSent / logCount : 0;
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
        int index = (int) Math.ceil(PERCENTILE_95 * sortedSizes.size()) - 1;
        return sortedSizes.get(index);
    }

    // Метод для подсчёта уникальных IP-адресов
    public int getUniqueIpCount() {
        return uniqueIps.size();
    }

    // Метод для получения самого популярного HTTP-метода
    public String getMostPopularMethod() {
        return methodFrequency.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("UNKNOWN");
    }
}
