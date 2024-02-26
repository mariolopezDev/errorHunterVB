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
                
            // Verificar si la línea excede los 90 caracteres
            if (lineText.length() > 90) {
                String lengthError = String.format("Error en línea %04d: la línea excede los 90 caracteres.", lineNumber);
                writer.write(lengthError);
                writer.newLine();
            }

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
            writer.write("------------------------------------");
            writer.newLine();

            //
            //Analiza si hay tokens mismatches y los reporta

            if (stats.getModuleProgramCount() != stats.getEndModuleCount()) {
                writer.write("Error: Mismatch en el número de declaraciones de Module y End Module.\n");
            }

            if (stats.getModuleProgramCount() == 0) {
                writer.write("Error: 'Module Program' inexistente.\n");
            } else if (stats.getModuleProgramCount() > 1) {
                writer.write("Error: Múltiples declaraciones de Module Program.\n");
            } 
            
            if (stats.getEndModuleCount() == 0) {
                writer.write("Error: 'End Module' inexistente.\n");
            } else if (stats.getEndModuleCount() > 1) {
                writer.write("Error: Múltiples declaraciones de End Module.\n");
            }
            
            if (stats.getSubMainCount() != stats.getEndSubCount()) {
                writer.write("Error: Mismatch en el número de declaraciones de Sub Main y End Sub.\n");
            }

            if (stats.getSubMainCount() > 1) {
                writer.write("Error: Múltiples declaraciones de Sub Main.\n");
            } else if (stats.getSubMainCount() == 0) {
                writer.write("Error: 'Sub Main' inexistente.\n");
            }

            if (stats.getEndSubCount() > 1) {
                writer.write("Error: Múltiples declaraciones de End Sub.\n");
            } else if (stats.getEndSubCount() == 0) {
                writer.write("Error: 'End Sub' inexistente.\n");
            }

            
 

            if (stats.getTryCount() != stats.getEndTryCount()) {
                writer.write("Error: Mismatch en el número de declaraciones de Try y End Try.\n");
            }
            
            if (stats.getTryCount() > stats.getEndTryCount()) {
                writer.write("Error: Falta declaración de 'End Try'.\n");
            } else if ((stats.getCatchCount() > stats.getTryCount()) || (stats.getTryCount() < stats.getEndTryCount())) {
                writer.write("Error: Falta declaración de 'Try'.\n");
            }

            if (stats.getWhileCount() != stats.getEndWhileCount()) {
                writer.write("Error: Mismatch en el número de declaraciones de While y End While.\n");
            }          

            if (stats.getWhileCount() > stats.getEndWhileCount()) {
                writer.write("Error: Falta declaración de 'End While'.");
            } else if (stats.getWhileCount() < stats.getEndWhileCount()) {
                writer.write("Error: Falta declaración de 'While'.");
            }

            writer.write("------------------------------------");
            writer.newLine();
        } catch (Exception e) {
            System.out.println("Error al escribir el archivo de errores: " + e.getMessage());
        }
    }
}
// Path: ErrorReporter.java
