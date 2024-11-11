package backend.academy;

public class ReportFormatter {

    private LogReport report;

    public ReportFormatter(LogReport report) {
        this.report = report;
    }

    public String formatToMarkdown() {
        StringBuilder sb = new StringBuilder();
        sb.append("# Log Report\n\n")
            .append("**Total Requests:** ").append(report.getTotalRequests()).append("\n\n")
            .append("**Most Frequent Resources:**\n");

        report.getMostFrequentResources().forEach((resource, count) -> {
            sb.append("- ").append(resource).append(": ").append(count).append("\n");
        });

        sb.append("\n**Most Frequent Status Codes:**\n");
        report.getMostFrequentStatusCodes().forEach((status, count) -> {
            sb.append("- ").append(status).append(": ").append(count).append("\n");
        });

        sb.append("\n**Average Response Size:** ").append(report.getAverageBytesSent()).append("\n")
            .append("**95th Percentile Response Size:** ").append(report.get95thPercentile()).append("\n");

        return sb.toString();
    }

    public String formatToAsciidoc() {
        StringBuilder sb = new StringBuilder();
        sb.append("= Log Report\n\n")
            .append("* Total Requests: ").append(report.getTotalRequests()).append("\n\n")
            .append("== Most Frequent Resources\n");

        report.getMostFrequentResources().forEach((resource, count) -> {
            sb.append("* ").append(resource).append(": ").append(count).append("\n");
        });

        sb.append("\n== Most Frequent Status Codes\n");
        report.getMostFrequentStatusCodes().forEach((status, count) -> {
            sb.append("* ").append(status).append(": ").append(count).append("\n");
        });

        sb.append("\n* Average Response Size: ").append(report.getAverageBytesSent()).append("\n")
            .append("* 95th Percentile Response Size: ").append(report.get95thPercentile()).append("\n");

        return sb.toString();
    }
}
