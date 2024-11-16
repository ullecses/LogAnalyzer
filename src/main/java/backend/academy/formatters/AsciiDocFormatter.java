package backend.academy.formatters;

public class AsciiDocFormatter extends AbstractReportFormatter {

    @Override
    protected String formatHeader(String header) {
        return String.format("== %s\n\n", header);
    }

    @Override
    protected String formatTableStart(String tableHeader) {
        StringBuilder sb = new StringBuilder("|===\n");
        String[] headers = tableHeader.split(" \\| ");

        for (String header : headers) {
            sb.append("| ").append(header).append(" ");
        }
        sb.append("\n");
        return sb.toString();
    }

    @Override
    protected String formatTableRow(String... columns) {
        return "| " + String.join(" | ", columns) + "\n";
    }

    @Override
    protected String formatTableEnd() {
        return "|===\n\n";
    }
}

