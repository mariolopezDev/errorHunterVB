import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// ErrorReporter.java
public class ErrorReporter {
    private final Map<Integer, String> errors = new LinkedHashMap<>();
    private final String basePath;

    public ErrorReporter(String basePath) {
        this.basePath = basePath;
    }

    public void report(int lineNumber, String errorMessage) {
        errors.put(lineNumber, "Error: " + errorMessage);
    }

    public void writeErrorsToFile(List<String> originalLines) throws IOException {
        String errorFilePath = basePath + "-Errores.txt";
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(errorFilePath))) {
            for (int i = 0; i < originalLines.size(); i++) {
                int lineNumber = i + 1;
                String lineText = originalLines.get(i);
                writer.write(String.format("%04d %s", lineNumber, lineText));
                writer.newLine();
                
                if (errors.containsKey(lineNumber)) {
                    writer.write(errors.get(lineNumber));
                    writer.newLine();
                }
            }
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
