import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

public class VBScriptLexer {
    private List<String> lines;
    private ArrayList<Token> tokens;
    private Map<Pattern, Token.Type> tokenPatterns;

    public VBScriptLexer(List<String> lines) {
        this.lines = lines;
        this.tokens = new ArrayList<>();
        initializeTokenPatterns();
    }

    // Inicializa los patrones de tokens para el análisis léxico
    private void initializeTokenPatterns() {
        tokenPatterns = new HashMap<>();

        
        // Reconocer comentarios y literales de string
        tokenPatterns.put(Pattern.compile("^\\s*'.*"), Token.Type.COMMENT);
        tokenPatterns.put(Pattern.compile("^\"(?:[^\"\\\\]|\\\\.)*\""), Token.Type.STRING_LITERAL);
        
        // Reconocer estructuras compuestas completas
        tokenPatterns.put(Pattern.compile("^\\s*Module\\s+Program\\s*\\b", Pattern.CASE_INSENSITIVE), Token.Type.MODULE_PROGRAM);
        tokenPatterns.put(Pattern.compile("^\\s*End\\s+Module\\b", Pattern.CASE_INSENSITIVE), Token.Type.END_MODULE);
        tokenPatterns.put(Pattern.compile("^\\s*Imports\\s+\\w[.\\w]*.*", Pattern.CASE_INSENSITIVE), Token.Type.IMPORT);
        tokenPatterns.put(Pattern.compile("^\\s*Sub\\s+Main.*", Pattern.CASE_INSENSITIVE), Token.Type.SUB_MAIN);
        tokenPatterns.put(Pattern.compile("^\\s*End\\s+Sub\\b", Pattern.CASE_INSENSITIVE), Token.Type.END_SUB);
        tokenPatterns.put(Pattern.compile("^\\s*While\\b", Pattern.CASE_INSENSITIVE), Token.Type.WHILE);
        tokenPatterns.put(Pattern.compile("^\\s*End\\s+While\\b", Pattern.CASE_INSENSITIVE), Token.Type.END_WHILE);
        tokenPatterns.put(Pattern.compile("^\\s*Try\\b", Pattern.CASE_INSENSITIVE), Token.Type.TRY);
        tokenPatterns.put(Pattern.compile("^\\s*Dim.*", Pattern.CASE_INSENSITIVE), Token.Type.DIM_STATEMENT);
        tokenPatterns.put(Pattern.compile("^\\s*Catch.*", Pattern.CASE_INSENSITIVE), Token.Type.CATCH_EXCEPTION);
        tokenPatterns.put(Pattern.compile("^\\s*End\\s+Try\\b", Pattern.CASE_INSENSITIVE), Token.Type.END_TRY);
        

        // Reconocer otros patrones
        //tokenPatterns.put(Pattern.compile("\\b\\w+\\b"), Token.Type.OTHER);

    }

    // Tokeniza las líneas de entrada
    public ArrayList<Token> tokenize() {
        int lineNumber = 1;
        for (String line : lines) {
            processLine(line, lineNumber++);
        }
        return tokens;
    }

    // Procesa cada línea para extraer tokens
    private void processLine(String line, int lineNumber) {
        System.out.println("Processing line " + lineNumber + ": " + line); //Temporal
        
        boolean isCommentLine = false;
        

        for (Map.Entry<Pattern, Token.Type> entry : tokenPatterns.entrySet()) {
            Matcher matcher = entry.getKey().matcher(line);
            while (matcher.find()) {
                if (isCommentLine) {
                    break;
                }
                String matchedText = matcher.group();

                //temporal para debug
                System.out.println("Matched text: " + matchedText + ", Token type: " + entry.getValue());
                
                
                // Verificar si el texto coincidente se solapa con algún token existente
                boolean overlapping = tokens.stream()
                    .anyMatch(token ->
                        token.getLineNumber() == lineNumber &&
                        token.getCharPosition() < matcher.end() &&
                        matcher.start() < token.getCharPosition() + token.getText().length());
                // Si no hay overlapping, añadir el token a la lista
                if (!overlapping) {
                    System.out.println("Token found: " + matchedText + entry.getValue()); // temporal
                    tokens.add(new Token(entry.getValue(), matchedText, lineNumber, matcher.start()));
                }
            }
        }
    }

}
