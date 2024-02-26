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

    private void initializeTokenPatterns() {
        tokenPatterns = new HashMap<>();

        
        // Recognize comments and string literals
        //tokenPatterns.put(Pattern.compile("^'[^\\w].*"), Token.Type.COMMENT);

        tokenPatterns.put(Pattern.compile("^\\s*'.*"), Token.Type.COMMENT);
        tokenPatterns.put(Pattern.compile("^\"(?:[^\"\\\\]|\\\\.)*\""), Token.Type.STRING_LITERAL);
        // Recognize complete compound structures
        tokenPatterns.put(Pattern.compile("^Module\\s+Program\\b", Pattern.CASE_INSENSITIVE), Token.Type.MODULE_PROGRAM);
        tokenPatterns.put(Pattern.compile("^End\\s+Module\\b", Pattern.CASE_INSENSITIVE), Token.Type.END_MODULE);
        tokenPatterns.put(Pattern.compile("^Imports\\s+\\w[.\\w]*.*", Pattern.CASE_INSENSITIVE), Token.Type.IMPORT);
        tokenPatterns.put(Pattern.compile("\\bDim\\s+(\\w+)\\s+As\\s+(String|Int|Boolean)\\b", Pattern.CASE_INSENSITIVE), Token.Type.DIM_STATEMENT);
        tokenPatterns.put(Pattern.compile("^\\s*\\bSub\\s+Main.*", Pattern.CASE_INSENSITIVE), Token.Type.SUB_MAIN);
        tokenPatterns.put(Pattern.compile("\\bEnd\\s+Sub\\b", Pattern.CASE_INSENSITIVE), Token.Type.END_SUB);
        tokenPatterns.put(Pattern.compile("\\bWhile\\b", Pattern.CASE_INSENSITIVE), Token.Type.WHILE);
        tokenPatterns.put(Pattern.compile("\\bEnd\\s+While\\b", Pattern.CASE_INSENSITIVE), Token.Type.END_WHILE);
        tokenPatterns.put(Pattern.compile("\\bTry\\b", Pattern.CASE_INSENSITIVE), Token.Type.TRY);
        tokenPatterns.put(Pattern.compile("\\bCatch\\s+(\\w+)\\s+As\\s+Exception\\b", Pattern.CASE_INSENSITIVE), Token.Type.CATCH_EXCEPTION);
        tokenPatterns.put(Pattern.compile("\\bEnd\\s+Try\\b", Pattern.CASE_INSENSITIVE), Token.Type.END_TRY);
        
        // Other patterns as necessary
        tokenPatterns.put(Pattern.compile("\\b\\w+\\s*=\\s*.*"), Token.Type.ASSIGNMENT); // For assignments

        // Recognize any other text as an OTHER token
        //tokenPatterns.put(Pattern.compile("\\b\\w+\\b"), Token.Type.OTHER);
    }

    public ArrayList<Token> tokenize() {
        int lineNumber = 1;
        for (String line : lines) {
            processLine(line, lineNumber++);
        }
        return tokens;
    }

    private void processLine(String line, int lineNumber) {
        System.out.println("Processing line " + lineNumber + ": " + line); // Debugging line
        
        boolean isCommentLine = false;
        

        for (Map.Entry<Pattern, Token.Type> entry : tokenPatterns.entrySet()) {
            Matcher matcher = entry.getKey().matcher(line);
            while (matcher.find()) {
                if (isCommentLine) {
                    break;
                }
                String matchedText = matcher.group();

                // Print out the matched text and the associated token type
                System.out.println("Matched text: " + matchedText + ", Token type: " + entry.getValue());
                
                
                // Check if the matched text overlaps with any existing token
                boolean overlapping = tokens.stream()
                    .anyMatch(token ->
                        token.getLineNumber() == lineNumber &&
                        token.getCharPosition() < matcher.end() &&
                        matcher.start() < token.getCharPosition() + token.getText().length());
                // If not overlapping, add the token
                if (!overlapping) {
                    System.out.println("Token found: " + matchedText + entry.getValue()); // Debugging line
                    tokens.add(new Token(entry.getValue(), matchedText, lineNumber, matcher.start()));
                }
            }
        }
    }

}
