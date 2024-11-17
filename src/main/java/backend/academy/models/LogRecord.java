package backend.academy.models;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter public class LogRecord {
    private String remoteAddress;    // IP-адрес клиента
    private String remoteUser;       // Имя пользователя
    private ZonedDateTime timeZoned; // Время запроса
    private String timeZonedStr;
    private String request;          // Запрос (включая метод, URL, версию HTTP)
    private int status;              // Код ответа
    private int bodyBytesSent;       // Размер ответа
    private String httpReferer;      // Реферер (если есть)
    private String httpUserAgent;    // User-Agent клиента

    @Override
    public String toString() {
        return "LogRecord{"
            + "remoteAddress='" + remoteAddress + '\''
            + ", remoteUser='" + remoteUser + '\''
            + ", timeLocal=" + timeZoned
            + ", request='" + request + '\''
            + ", status=" + status
            + ", bodyBytesSent=" + bodyBytesSent
            + ", httpReferer='" + httpReferer + '\''
            + ", httpUserAgent='" + httpUserAgent + '\''
            + '}';
    }
}

