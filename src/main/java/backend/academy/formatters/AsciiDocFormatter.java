package backend.academy.formatters;

public class AsciiDocFormatter extends AbstractReportFormatter {

    @Override public String formatHeader(String header) {
        return String.format("== %s\n\n", header);
    }

    @Override public String formatTableStart(String tableHeader) {
        StringBuilder sb = new StringBuilder("|===\n");
        String[] headers = tableHeader.split(" \\| ");

        for (String header : headers) {
            sb.append("| ").append(header).append(" ");
        }
        sb.append("\n");
        return sb.toString();
    }

    @Override public String formatTableRow(String... columns) {
        return "| " + String.join(" | ", columns) + "\n";
    }

    @Override public String formatTableEnd() {
        return "|===\n\n";
    }
}

