package backend.academy.formatters;

public class MarkdownFormatter extends AbstractReportFormatter {

    @Override
    protected String formatHeader(String header) {
        return String.format("#### %s\n\n", header);
    }

    @Override
    protected String formatTableStart(String tableHeader) {
        String[] headers = tableHeader.split(" \\| ");
        StringBuilder sb = new StringBuilder("| ");
        StringBuilder align = new StringBuilder("|");

        for (String header : headers) {
            sb.append(header).append(" | ");
            align.append(":-------:|");
        }

        return sb + "\n" + align + "\n";
    }

    @Override
    protected String formatTableRow(String... columns) {
        return "| " + String.join(" | ", columns) + " |\n";
    }

    @Override
    protected String formatTableEnd() {
        return "\n";
    }
}
