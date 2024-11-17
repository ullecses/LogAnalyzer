package backend.academy.utils;

import lombok.Getter;

public class FileNameUtil {
    static String adoc = "adoc";

    private FileNameUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    @Getter public enum FileFormat {
        MARKDOWN("markdown", "md"),
        ASCIIDOC(adoc, adoc);

        private final String formatName;
        private final String fileExtension;

        FileFormat(String formatName, String fileExtension) {
            this.formatName = formatName;
            this.fileExtension = fileExtension;
        }
    }

    public static String getOutputFileName(String format) {
        FileFormat fileFormat = getOutputFileFormat(format);
        return "report." + fileFormat.fileExtension();
    }

    public static FileFormat getOutputFileFormat(String format) {
        if (format == null) {
            return FileFormat.MARKDOWN; // По умолчанию Markdown
        }
        for (FileFormat fileFormat : FileFormat.values()) {
            if (fileFormat.formatName().equalsIgnoreCase(format)) {
                return fileFormat;
            }
        }
        throw new IllegalArgumentException("Unsupported format: " + format);
    }
}
