public class ParserStatistics {
    public int commentCount;
    public int dimStatementCount;
    public int moduleProgramCount;
    public int endModuleCount;
    public int importCount;
    public int subMainCount;
    public int endSubCount;
    public int tryCount;
    public int catchCount;
    public int endTryCount;
    public int whileCount;
    public int endWhileCount;

    // Constructor
    public ParserStatistics() {
        this.commentCount = 0;
        this.dimStatementCount = 0;
        this.moduleProgramCount = 0;
        this.endModuleCount = 0;
        this.importCount = 0;
        this.subMainCount = 0;
        this.endSubCount = 0;
        this.tryCount = 0;
        this.catchCount = 0;
        this.endTryCount = 0;
        this.whileCount = 0;
        this.endWhileCount = 0;
    }

    // Getters
    public int getCommentCount() {
        return commentCount;
    }

    public int getDimStatementCount() {
        return dimStatementCount;
    }

    public int getModuleProgramCount() {
        return moduleProgramCount;
    }

    public int getEndModuleCount() {
        return endModuleCount;
    }

    public int getImportCount() {
        return importCount;
    }

    public int getSubMainCount() {
        return subMainCount;
    }

    public int getEndSubCount() {
        return endSubCount;
    }

    public int getTryCount() {
        return tryCount;
    }

    public int getCatchCount() {
        return catchCount;
    }

    public int getEndTryCount() {
        return endTryCount;
    }

    public int getWhileCount() {
        return whileCount;
    }

    public int getEndWhileCount() {
        return endWhileCount;
    }

    // Increment methods
    public void incrementCommentCount() {
        this.commentCount++;
    }

    public void incrementDimStatementCount() {
        this.dimStatementCount++;
    }

    public void incrementModuleProgramCount() {
        this.moduleProgramCount++;
    }

    public void incrementEndModuleCount() {
        this.endModuleCount++;
    }

    public void incrementImportCount() {
        this.importCount++;
    }

    public void incrementSubMainCount() {
        this.subMainCount++;
    }

    public void incrementEndSubCount() {
        this.endSubCount++;
    }   

    public void incrementTryCount() {
        this.tryCount++;
    }

    public void incrementCatchCount() {
        this.catchCount++;
    }

    public void incrementEndTryCount() {
        this.endTryCount++;
    }

    public void incrementWhileCount() {
        this.whileCount++;
    }

    public void incrementEndWhileCount() {
        this.endWhileCount++;
    }

    // Statistics method
    public String getStatistics() {
        String statistics = "Número de Comentarios: " + commentCount + "\n" +
        "Número de Declaraciones DIM: " + dimStatementCount + "\n" +
        "Número de Módulos Program: " + moduleProgramCount + "\n" +
        "Número de End Module: " + endModuleCount + "\n" +
        "Número de Imports: " + importCount + "\n" +
        "Número de Sub Main: " + subMainCount + "\n" +
        "Número de End Sub: " + endSubCount + "\n" +
        "Número de Try: " + tryCount + "\n" +
        "Número de Catch: " + catchCount + "\n" +
        "Número de End Try: " + endTryCount + "\n" +
        "Número de While: " + whileCount + "\n" +
        "Número de End While: " + endWhileCount + "\n";
        return statistics;
    }
}
