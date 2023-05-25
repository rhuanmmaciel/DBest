package dsl.entities;

import dsl.utils.DslUtils;

public final class VariableDeclaration extends Command{

	private String variable;
	private Expression<?> expression;
	
	public VariableDeclaration(String command) {
	
		super(command);
		recognizer(command);
		
	}
	
	private void recognizer(String command) {
		
		variable = command.substring(0, command.indexOf("=")).strip();
		
		String stringExpression = command.substring(command.indexOf("=") + 1);
		
		expression = DslUtils.expressionRecognizer(stringExpression);
		
		
	}
	
	public String getVariable() {

		return variable;
		
	}
	
	public Expression<?> getContent(){
		
		return expression;
		
	}

}
