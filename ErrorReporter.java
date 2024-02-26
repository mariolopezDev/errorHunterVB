import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(errorFilePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
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



    public void writeHeader(String filename) throws IOException {
        String errorFilePath = basePath + "-Errores.txt";
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(errorFilePath))) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            writer.write("Informe de Análisis de " + filename);
            writer.newLine();
            writer.write("Fecha de Análisis: " + now.format(formatter));
            writer.newLine();
            writer.write("------------------------------------");
            writer.newLine();
        }
    }



    public void reportSummary(ParserStatistics stats) throws IOException {
        String errorFilePath = basePath + "-Errores.txt";
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(errorFilePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write("Resumen del Análisis:");
            writer.newLine();
            writer.write("Número de Comentarios: " + stats.getCommentCount());
            writer.newLine();
            writer.write("Número de Declaraciones DIM: " + stats.getDimStatementCount());
            writer.newLine();
            //
            //Analiza si hay tokens mismatches y los reporta

            
            if (stats.getModuleProgramCount() == 0) {
                writer.write("Error: Falta un Module Program.\n");
            } else if (stats.getModuleProgramCount() > 1) {
                writer.write("Error: Múltiples declaraciones de Module Program.\n");
            } 

            if (stats.getModuleProgramCount() != stats.getEndModuleCount()) {
                writer.write("Error: Mismatch en el número de declaraciones de Module y End Module.\n");
            }


            if (stats.getSubMainCount() != stats.getEndSubCount()) {
                writer.write("Error: Mismatch en el número de declaraciones de Sub Main y End Sub.\n");
            }
            if (stats.getTryCount() != stats.getEndTryCount()) {
                writer.write("Error: Mismatch en el número de declaraciones de Try y End Try.\n");
            }

            //Reconoce si falta el open o el close de un bloque
            if (stats.getModuleProgramCount() > stats.getEndModuleCount()) {
                writer.write("Error: Falta un End Module.\n");
            } else if (stats.getModuleProgramCount() < stats.getEndModuleCount()) {
                writer.write("Error: Falta un Module Program.\n");
            }
            
            if (stats.getSubMainCount() > stats.getEndSubCount()) {
                writer.write("Error: Falta el End Sub del Sub Main.\n");
            } else if (stats.getSubMainCount() < stats.getEndSubCount()) {
                writer.write("Error: Falta un Sub Main.\n");
            }
            
            
            if (stats.getTryCount() > stats.getEndTryCount()) {
                writer.write("Error: Falta un End Try.");
            } else if (stats.getTryCount() < stats.getEndTryCount()) {
                writer.write("Error: Falta un Try.\n");
            }

            if (stats.getWhileCount() != stats.getEndWhileCount()) {
                writer.write("Error: Mismatch en el número de declaraciones de While y End While.\n");
            }

            if (stats.getWhileCount() > stats.getEndWhileCount()) {
                writer.write("Error: Falta un End While.");
            } else if (stats.getWhileCount() < stats.getEndWhileCount()) {
                writer.write("Error: Falta un While.");
            }


            writer.newLine();
            writer.write("------------------------------------");
            writer.newLine();
        } catch (Exception e) {
            System.out.println("Error al escribir el archivo de errores: " + e.getMessage());
        }
    }
}
// Path: ErrorReporter.java
