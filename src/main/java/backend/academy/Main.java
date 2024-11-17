package backend.academy;

import backend.academy.analyzer.LogAnalyzer;
import backend.academy.parsers.LogArgumentsParser;
import backend.academy.utils.FileNameUtil;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    public static void main(String[] args) {
        try {
            LogArgumentsParser parser = new LogArgumentsParser(args);
            String outputFileName = FileNameUtil.getOutputFileName(parser.format());

            try (PrintStream printStream = new PrintStream(new FileOutputStream(outputFileName))) {
                LogAnalyzer analyzer = new LogAnalyzer(printStream, parser);
                analyzer.analyze();
            }
        } catch (FileNotFoundException e) {
            System.err.println("Ошибка при создании файла: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка при анализе логов: " + e.getMessage());
        }
    }
}
