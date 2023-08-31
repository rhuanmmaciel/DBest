package dsl.entities;

public final class VariableDeclaration extends Command{

	private String variable;
	private String expression;
	
	public VariableDeclaration(String command) {
	
		super(command);
		recognizer(command);
		
	}
	
	private void recognizer(String command) {
		
		variable = command.substring(0, command.indexOf("=")).strip();

        expression = command.substring(command.indexOf("=") + 1);
		
	}
	
	public String getVariable() {

		return variable;
		
	}
	
	public String getExpression(){
		
		return expression;
		
	}

}
