package backend.academy.io;

import java.io.PrintStream;

public class OutputHandler {
    private final PrintStream printStream;

    public OutputHandler(PrintStream printStream) {
        this.printStream = printStream;
    }

    public void printLine(String message) {
        printStream.println(message);
    }
}
