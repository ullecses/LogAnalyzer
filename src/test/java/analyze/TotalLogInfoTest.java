package analyze;

import backend.academy.analyzer.TotalLogInfo;
import backend.academy.models.LogRecord;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TotalLogInfoTest {

    @Test
    void shouldCalculateStatisticsCorrectly() {
        // Arrange
        TotalLogInfo totalLogInfo = new TotalLogInfo();
        LogRecord record1 = createLogRecord("GET /index.html HTTP/1.1", "192.168.0.1", 200, 500);
        LogRecord record2 = createLogRecord("POST /api/data HTTP/1.1", "192.168.0.2", 404, 1000);

        // Act
        totalLogInfo.addLogRecord(record1);
        totalLogInfo.addLogRecord(record2);

        // Assert
        assertEquals(2, totalLogInfo.totalRequests());
        assertEquals(2, totalLogInfo.getUniqueIpCount());
        assertEquals(750, totalLogInfo.getAverageBytesSent());
    }

    private LogRecord createLogRecord(String request, String ip, int status, int bodyBytesSent) {
        LogRecord logRecord = new LogRecord();
        logRecord.request(request);
        logRecord.remoteAddress(ip);
        logRecord.status(status);
        logRecord.bodyBytesSent(bodyBytesSent);
        return logRecord;
    }
}
