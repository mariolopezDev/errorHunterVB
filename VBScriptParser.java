import java.util.List;
import java.util.Stack;

public class VBScriptParser {
    private List<Token> tokens;
    private ErrorReporter errorReporter;

    private Stack<Token> tryCatchStack = new Stack<>();

    public VBScriptParser(List<Token> tokens, ErrorReporter errorReporter) {
        this.tokens = tokens;
        this.errorReporter = errorReporter;
    }

    public void parse() {
        // First, validate the presence of "Module Program" and its correct position
        //validateModuleProgram();
        validateModuleStructure();


        // After the initial validation, continue with further parsing
        // and checking for additional structures and syntax rules
        for (Token token : tokens) {
            // Depending on the type of token, we perform different checks and validations
            switch (token.getType()) {
                case MODULE_PROGRAM:
                    // We've already checked for 'Module Program' declaration, so we can ignore it here
                    break;
                case DIM_STATEMENT:
                case SUB_MAIN:
                    break;
                case END_SUB:
                    break;
                case WHILE:
                    break;
                case END_WHILE:
                    break;
                case TRY:
                    tryCatchStack.push(token);
                    break;
                case CATCH_EXCEPTION:
                    if (tryCatchStack.isEmpty() || tryCatchStack.peek().getType() != Token.Type.TRY) {
                        errorReporter.report(token.getLineNumber(), "CATCH without a preceding TRY.");
                    } else {
                        // Correctamente encontramos un TRY para este CATCH
                        tryCatchStack.pop(); // Eliminamos el TRY ya que encontramos su CATCH correspondiente
                        tryCatchStack.push(token); // Ahora el CATCH está en la cima de la pila
                    }
                    break;
                case END_TRY:
                    if (tryCatchStack.isEmpty() || tryCatchStack.peek().getType() != Token.Type.CATCH_EXCEPTION) {
                        errorReporter.report(token.getLineNumber(), "END TRY without a preceding CATCH.");
                    } else {
                        // Correctamente encontramos un CATCH para este END TRY
                        tryCatchStack.pop(); // Eliminamos el CATCH ya que encontramos su END TRY correspondiente
                    }
                    break;
                case ASSIGNMENT:
                case OTHER:
                    // Perform specific validations for each type of token
                    // For example:
                    // validateSubMain(token);
                    // validateDimStatement(token);
                    // etc.
                    break;
                default:
                    // Handle any other tokens or unexpected cases
                    break;
            }
            
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
            System.out.println("Processing token: " + token.getType() + " - " + token.getText());
            
            if (moduleEndFound && token.getType() != Token.Type.COMMENT) {
                // Report error if we find tokens other than COMMENT after 'End Module'
                errorReporter.report(token.getLineNumber(), "Invalid token found after 'End Module'. Only comments are allowed.");
            }
            
            
            if (token.getType() == Token.Type.MODULE_PROGRAM) {
                if (moduleStartFound) {
    
                    errorReporter.report(token.getLineNumber(), "Multiple 'Module Program' declarations found.");
                } else {
                    moduleStartFound = true;
                }
            } 
            if (token.getType() == Token.Type.END_MODULE) {
                if (moduleEndFound) {
            
                    errorReporter.report(token.getLineNumber(), "Multiple 'End Module' declarations found.");
                } else {
                    moduleEndFound = true;
                }
            } 
            
            if (!moduleStartFound && (token.getType() != Token.Type.IMPORT && token.getType() != Token.Type.COMMENT)) {
                if (token.getType() == Token.Type.END_MODULE){
                    errorReporter.report(token.getLineNumber(), "'End Module' declaration found before 'Module Program'.");
                } else {
                    errorReporter.report(token.getLineNumber(), "Invalid token found before 'Module Program'. Only 'Imports' and comments are allowed.");
                }
            }

            if (token.getLineNumber() == 36){
                System.out.println("Token: " + token.getType() + " - " + token.getText());
            }
            
        }

        // If 'Module Program' was not found, report it
        if (!moduleStartFound) {
            errorReporter.report(1, "'Module Program' declaration is missing.");
        }

        // If 'End Module' was not found, report it
        if (!moduleEndFound) {

            errorReporter.report(1, "'End Module' declaration is missing.");
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
                    // Found a second "Module Program", stop and let the duplicate checker handle it
                    break;
                }
            } else if (!firstModuleProgramFound && (token.getType() != Token.Type.COMMENT && token.getType() != Token.Type.IMPORT)) {
                // Log error: no tokens other than comments or imports are allowed before "Module Program"
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
                    // Log error: Duplicate "Module Program" declaration
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