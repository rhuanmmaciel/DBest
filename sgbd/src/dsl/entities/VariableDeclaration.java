package dsl.entities;

public final class VariableDeclaration extends Command {

    private String variable;

    private String expression;

    public VariableDeclaration(String command) {
        super(command);

        this.recognizer(command);
    }

    private void recognizer(String command) {
        this.variable = command.substring(0, command.indexOf("=")).strip();
        this.expression = command.substring(command.indexOf("=") + 1);
    }

    public String getVariable() {
        return this.variable;
    }

    public String getExpression() {
        return this.expression;
    }
}
