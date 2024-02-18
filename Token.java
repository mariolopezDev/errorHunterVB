// Purpose: Token class for the lexer.
public class Token {
    enum Type {
        MODULE_PROGRAM, // 'Module Program'
        END_MODULE,     // 'End Module'
        IMPORT,         // 'Import'
        DIM_STATEMENT,  // 'Dim identifier As Type'
        SUB_MAIN,       // 'Sub Main()'
        END_SUB,        // 'End Sub'
        WHILE,          // 'While'
        END_WHILE,      // 'End While'
        TRY,            // 'Try'
        END_TRY,        // 'End Try'
        CATCH_EXCEPTION,// 'Catch ... As Exception'
        COMMENT,        // 'Comment line'
        STRING_LITERAL, // '"String literal"'
        ASSIGNMENT,     // 'identifier = ...'

        OTHER,          // Otros tokens'
    }

    private final Type type;
    private final String text;
    private final int lineNumber;
    private final int charPosition;

    public Token(Type type, String text, int lineNumber, int charPosition) {
        this.type = type;
        this.text = text;
        this.lineNumber = lineNumber;
        this.charPosition = charPosition;
    }
    
    // Getters
    public Type getType() { return type; }
    public String getText() { return text; }
    public int getLineNumber() { return lineNumber; }
    public int getCharPosition() { return charPosition; }
}
