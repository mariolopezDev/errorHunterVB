import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String vbFilePath;

        if (args.length == 0) {
            // No arguments, use default path
            vbFilePath = "test/default.vb";
            System.out.println("No file specified. Using default file: " + vbFilePath);
        } else {
            // Use the provided argument
            vbFilePath = args[0];
        }

        String basePath = vbFilePath.substring(0, vbFilePath.lastIndexOf('.'));

        try {
            List<String> lines = Files.readAllLines(Paths.get(vbFilePath));
            ErrorReporter errorReporter = new ErrorReporter(basePath);

            VBScriptLexer lexer = new VBScriptLexer(lines);
            ArrayList<Token> tokens = lexer.tokenize();

            VBScriptParser parser = new VBScriptParser(tokens, errorReporter);
            parser.parse();
            
            errorReporter.writeHeader(vbFilePath);
            errorReporter.reportSummary(parser.stats);
            errorReporter.writeErrorsToFile(lines);
            if (errorReporter.hasErrors()) {
                System.out.println("Se encontraron errores. Revise el archivo " + basePath + "-Errores.txt");
            } else {
                System.out.println("Análisis completado sin errores.");
            }

        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            System.exit(2);
        }
    }
}
