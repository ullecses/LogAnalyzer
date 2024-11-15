package backend.academy;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter public class LogRecord {

    // Геттеры и сеттеры
    private String remoteAddress;       // IP-адрес клиента
    private String remoteUser;       // Имя пользователя
    private ZonedDateTime timeZoned; // Время запроса
    private String request;          // Запрос (включая метод, URL, версию HTTP)
    private int status;              // Код ответа
    private int bodyBytesSent;       // Размер ответа
    private String httpReferer;      // Реферер (если есть)
    private String httpUserAgent;    // User-Agent клиента

    // Конструктор
    public LogRecord(String remoteAddress, String remoteUser, ZonedDateTime timeZoned,
        String request, int status, int bodyBytesSent,
        String httpReferer, String httpUserAgent) {
        this.remoteAddress = remoteAddress;
        this.remoteUser = remoteUser;
        this.timeZoned = timeZoned;
        this.request = request;
        this.status = status;
        this.bodyBytesSent = bodyBytesSent;
        this.httpReferer = httpReferer;
        this.httpUserAgent = httpUserAgent;
    }

    @Override
    public String toString() {
        return "LogRecord{" +
            "remoteAddress='" + remoteAddress + '\'' +
            ", remoteUser='" + remoteUser + '\'' +
            ", timeLocal=" + timeZoned +
            ", request='" + request + '\'' +
            ", status=" + status +
            ", bodyBytesSent=" + bodyBytesSent +
            ", httpReferer='" + httpReferer + '\'' +
            ", httpUserAgent='" + httpUserAgent + '\'' +
            '}';
    }
}

