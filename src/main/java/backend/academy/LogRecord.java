package backend.academy;

import java.time.LocalDateTime;

public class LogRecord {

    private String remoteAddr;       // IP-адрес клиента
    private String remoteUser;       // Имя пользователя
    private LocalDateTime timeLocal; // Время запроса
    private String request;          // Запрос (включая метод, URL, версию HTTP)
    private int status;              // Код ответа
    private int bodyBytesSent;       // Размер ответа
    private String httpReferer;      // Реферер (если есть)
    private String httpUserAgent;    // User-Agent клиента

    // Конструктор
    public LogRecord(String remoteAddr, String remoteUser, LocalDateTime timeLocal,
        String request, int status, int bodyBytesSent,
        String httpReferer, String httpUserAgent) {
        this.remoteAddr = remoteAddr;
        this.remoteUser = remoteUser;
        this.timeLocal = timeLocal;
        this.request = request;
        this.status = status;
        this.bodyBytesSent = bodyBytesSent;
        this.httpReferer = httpReferer;
        this.httpUserAgent = httpUserAgent;
    }

    // Геттеры и сеттеры
    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    public LocalDateTime getTimeLocal() {
        return timeLocal;
    }

    public void setTimeLocal(LocalDateTime timeLocal) {
        this.timeLocal = timeLocal;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBodyBytesSent() {
        return bodyBytesSent;
    }

    public void setBodyBytesSent(int bodyBytesSent) {
        this.bodyBytesSent = bodyBytesSent;
    }

    public String getHttpReferer() {
        return httpReferer;
    }

    public void setHttpReferer(String httpReferer) {
        this.httpReferer = httpReferer;
    }

    public String getHttpUserAgent() {
        return httpUserAgent;
    }

    public void setHttpUserAgent(String httpUserAgent) {
        this.httpUserAgent = httpUserAgent;
    }

    @Override
    public String toString() {
        return "LogRecord{" +
            "remoteAddr='" + remoteAddr + '\'' +
            ", remoteUser='" + remoteUser + '\'' +
            ", timeLocal=" + timeLocal +
            ", request='" + request + '\'' +
            ", status=" + status +
            ", bodyBytesSent=" + bodyBytesSent +
            ", httpReferer='" + httpReferer + '\'' +
            ", httpUserAgent='" + httpUserAgent + '\'' +
            '}';
    }
}

