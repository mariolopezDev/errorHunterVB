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

    
    public VBScriptParser(List<Token> tokens, ErrorReporter errorReporter) {
        this.tokens = tokens;
        this.errorReporter = errorReporter;
    }

    /**
     * parse() - Realiza el análisis del VBScript, identificando errores y llevando el conteo de ciertos elementos.
     */
    public void parse() {


        int subMainCount = 0; // Contador para Sub Main

        validateModuleStructure();

        for (Token token : tokens) {
            switch (token.getType()) {
                case COMMENT:
                    stats.commentCount++;
                    break;
                case DIM_STATEMENT:
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
                        errorReporter.report(token.getLineNumber(), "END TRY without a preceding CATCH.");
                    } else {
                        // Correctamente encontramos un CATCH para este END TRY
                        tryCatchStack.pop(); // Eliminamos el CATCH ya que encontramos su END TRY correspondiente
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
            errorReporter.report(unclosedToken.getLineNumber(), "Unclosed " + unclosedToken.getType() + ".");
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
    
                    errorReporter.report(token.getLineNumber(), "Multiples declaraciones de 'Module Program' encontradas.");
                } else {
                    moduleStartFound = true;
                }
            } 
            if (token.getType() == Token.Type.END_MODULE) {
                if (moduleEndFound) {
            
                    errorReporter.report(token.getLineNumber(), "Multiples declaraciones de 'End Module' encontradas.");
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

        if (!moduleStartFound) {
            errorReporter.report(1, "Falta declaración de 'Module Program'.");
        }

        if (!moduleEndFound) {
            errorReporter.report(1, "Falta declaración de 'End Module'.");
        }
    }

    private void validateSubMain(Token token) {
        String text = token.getText();
        if (!text.matches("\\bSub\\s+Main\\s*\\(\\s*([^()]*(\\(\\s*[^()]*\\s*\\))?[^()]*)\\s*\\)\\s*$")) {
            errorReporter.report(token.getLineNumber(), "Declaración SUB MAIN inválida.");
        }
    }
    

    private void validateModuleProgram() {
        boolean firstModuleProgramFound = checkForFirstModuleProgram();
        boolean duplicateModuleProgramFound = checkForDuplicateModuleProgramDeclarations();
    
        // Report missing "Module Program" only if it was not found and no duplicates were found
        if (!firstModuleProgramFound && !duplicateModuleProgramFound) {
            reportMissingModuleProgram();
        }
    }
    
    
    private boolean checkForFirstModuleProgram() {
        boolean firstModuleProgramFound = false;
    
        for (Token token : tokens) {
            if (token.getType() == Token.Type.MODULE_PROGRAM) {
                if (!firstModuleProgramFound) {
                    firstModuleProgramFound = true;
                } else {
                    break;
                }
            } else if (!firstModuleProgramFound && (token.getType() != Token.Type.COMMENT && token.getType() != Token.Type.IMPORT)) {
                errorReporter.report(token.getLineNumber(), "Only comments or imports are allowed before 'Module Program'.");
            }
        }
    
        return firstModuleProgramFound;
    }
    
    private boolean checkForDuplicateModuleProgramDeclarations() {
        boolean firstModuleProgramFound = false;
        boolean duplicateFound = false;
    
        for (Token token : tokens) {
            if (token.getType() == Token.Type.MODULE_PROGRAM) {
                if (firstModuleProgramFound) {
                    errorReporter.report(token.getLineNumber(), "'Module Program' has already been declared.");
                    duplicateFound = true;
                } else {
                    firstModuleProgramFound = true; // Mark the first "Module Program" found
                }
            }
        }
    
        return duplicateFound;
    }
    
    
    private void reportMissingModuleProgram() {
        // If "Module Program" is missing, report it
        errorReporter.report(1, "'Module Program' declaration is missing.");
    }
}