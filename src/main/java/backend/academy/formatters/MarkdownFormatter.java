package backend.academy.formatters;

public class MarkdownFormatter extends AbstractReportFormatter {

    private static final String PIPE = " | ";

    @Override public String formatHeader(String header) {
        return String.format("#### %s\n\n", header);
    }

    @Override public String formatTableStart(String tableHeader) {
        String[] headers = tableHeader.split(" \\| ");
        StringBuilder sb = new StringBuilder("| ");
        StringBuilder align = new StringBuilder("|");

        for (String header : headers) {
            sb.append(header).append(PIPE);
            align.append(":-------:|");
        }

        return sb + "\n" + align + "\n";
    }

    @Override public String formatTableRow(String... columns) {
        return "| " + String.join(PIPE, columns) + " |\n";
    }

    @Override public String formatTableEnd() {
        return "\n";
    }
}
