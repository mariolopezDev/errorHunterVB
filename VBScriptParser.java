import java.util.List;
import java.util.Stack;

/**
 * Clase VBScriptParser para analizar y reportar errores en VBScript.
 */

public class VBScriptParser {
    private List<Token> tokens;
    private ErrorReporter errorReporter;

    public ParserStatistics stats = new ParserStatistics();

    private Stack<Token> tryCatchStack = new Stack<>();
    private Stack<Token> subMainStack = new Stack<>();
    private Stack<Token> whileStack = new Stack<>();

    
    public VBScriptParser(List<Token> tokens, ErrorReporter errorReporter) {
        this.tokens = tokens;
        this.errorReporter = errorReporter;
    }

    /**
     * parse() - Realiza el análisis del VBScript, identificando errores y llevando el conteo de ciertos elementos.
     */
    public void parse() {


        validateModuleStructure();

        for (Token token : tokens) {
            switch (token.getType()) {
                case COMMENT:
                    stats.commentCount++;
                    break;
                case DIM_STATEMENT:
                if (stats.moduleProgramCount < 1) {
                    errorReporter.report(token.getLineNumber(), "Declaración DIM encontrada antes de 'Module Program'.");
                }
                    validateDimStatement(token);
                    stats.dimStatementCount++;
                    break;
                case MODULE_PROGRAM:
                    stats.moduleProgramCount++;
                    break;
                case END_MODULE:
                    stats.endModuleCount++;
                    break;
                case IMPORT:
                    stats.importCount++;
                    if (stats.moduleProgramCount > 0) {
                        errorReporter.report(token.getLineNumber(), "'Imports' debe ser declarado antes de Module Program.");
                    }
                    break;
                case SUB_MAIN:
                    stats.subMainCount++;
                    validateSubMain(token);
                    subMainStack.push(token);
                    break;
                case END_SUB:
                    stats.endSubCount++;
                    if (subMainStack.isEmpty()) {
                        errorReporter.report(token.getLineNumber(), "END SUB sin un SUB MAIN correspondiente.");
                    } else {
                        subMainStack.pop();
                    }
                    break;
                case TRY:
                    stats.tryCount++;
                    tryCatchStack.push(token);
                    break;
                case CATCH_EXCEPTION:
                    stats.catchCount++;
                    validateCatchStatement(token);
                    if (tryCatchStack.isEmpty() || tryCatchStack.peek().getType() != Token.Type.TRY) {
                        errorReporter.report(token.getLineNumber(), "CATCH sin un TRY previo.");
                    } else {
                        // Correctamente encontramos un TRY para este CATCH
                        tryCatchStack.pop(); // Eliminamos el TRY ya que encontramos su CATCH correspondiente
                        tryCatchStack.push(token); // Ahora el CATCH está en la cima de la pila
                    }
                    break;
                case END_TRY:
                    stats.endTryCount++;
                    if (tryCatchStack.isEmpty() || tryCatchStack.peek().getType() != Token.Type.CATCH_EXCEPTION) {
                        errorReporter.report(token.getLineNumber(), "END TRY sin TRY o CATCH.");
                    } else {
                        // Correctamente encontramos un CATCH para este END TRY
                        tryCatchStack.pop(); // Eliminamos el CATCH ya que encontramos su END TRY correspondiente
                    }
                    break;
                case WHILE:
                    stats.whileCount++;
                    whileStack.push(token);
                    break;
                case END_WHILE:
                    stats.endWhileCount++;
                    if (whileStack.isEmpty()) {
                        errorReporter.report(token.getLineNumber(), "END WHILE sin un WHILE correspondiente.");
                    } else {
                        whileStack.pop();
                    }
                    break;
                // Otros casos y validaciones específicas aquí
                default:
                    break;
            }
            
        }

        openStructuresValidation();
    }

    private void openStructuresValidation() {
        // Verificar si hay estructuras Sub Main sin cerrar
        if (!subMainStack.isEmpty()) {
            Token unclosedToken = subMainStack.peek();
            errorReporter.report(unclosedToken.getLineNumber(), "SUB MAIN sin cerrar.");
        }

        // Verificar si hay estructuras TRY-CATCH sin cerrar
        if (!tryCatchStack.isEmpty()) {
            Token unclosedToken = tryCatchStack.peek();
            errorReporter.report(unclosedToken.getLineNumber(), "Estructura " + unclosedToken.getType() + " sin cerrar. Revise si existe End Try y Catch correspondientes.");
        }

        // Verificar si hay estructuras WHILE sin cerrar
        if (!whileStack.isEmpty()) {
            Token unclosedToken = whileStack.peek();
            errorReporter.report(unclosedToken.getLineNumber(), "Estructura " + unclosedToken.getType() + " sin cerrar.");
        }
        
    }


    private void validateModuleStructure() {

        boolean moduleStartFound = false;
        boolean moduleEndFound = false;
        
        for (Token token : tokens) {
            System.out.println("Revisando token: " + token.getType() + " - " + token.getText());
            if (moduleEndFound && token.getType() != Token.Type.COMMENT) {
                errorReporter.report(token.getLineNumber(), "Token invalido encontrado despues de 'End Module'. Solo comentarios son permitidos");
            }
            
            
            if (token.getType() == Token.Type.MODULE_PROGRAM) {
                if (moduleStartFound) {
    
                    errorReporter.report(token.getLineNumber(), "'Module Program' duplicado.");
                } else {
                    moduleStartFound = true;
                }
            } 
            if (token.getType() == Token.Type.END_MODULE) {
                if (moduleEndFound) {
            
                    errorReporter.report(token.getLineNumber(), "'End Module' duplicado.");
                } else {
                    moduleEndFound = true;
                }
            } 
            
            if (!moduleStartFound && (token.getType() != Token.Type.IMPORT && token.getType() != Token.Type.COMMENT)) {
                if (token.getType() == Token.Type.END_MODULE){
                    errorReporter.report(token.getLineNumber(), "Declaración 'End Module' encontrada antes de 'Module Program'.");
                } else {
                    errorReporter.report(token.getLineNumber(), "Token invalido encontrado antes de 'Module Program'. Solo 'Imports' y comentarios son permitidos.");
                }
            }            
        }
    }
    private void validateSubMain(Token token) {
        String text = token.getText().replaceAll("\\s+", "");
        int balance = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(') balance++;
            else if (c == ')') balance--;
            if (balance < 0) break;
        }
        if (!text.startsWith("SubMain(") || balance != 0) {
            errorReporter.report(token.getLineNumber(), "Formato incorrecto en SUB MAIN.");
        }
    }

    private void validateCatchStatement(Token token) {
        String text = token.getText().trim();
        // La expresión regular verifica que después de 'Catch' haya un identificador alfanumerico seguido de 'As Exception'
        String catchPattern = "Catch\\s+[a-zA-Z]\\w*\\s+As\\s+Exception";
    
        if (!text.matches(catchPattern)) {
            errorReporter.report(token.getLineNumber(), "Formato incorrecto en CATCH.");
        }
    }

    private void validateDimStatement(Token token) {
        String text = token.getText().trim();
        //Dim [variableName] As [Type]'
        // [variableName] empieza con una letra y puede contener números y letras adicionales.
        String dimPattern = "Dim\\s+[a-zA-Z]\\w*\\s+As\\s+.*";
    
        if (!text.matches(dimPattern)) {
            errorReporter.report(token.getLineNumber(), "Formato incorrecto en DIM.");
        }
    }
    
    
}