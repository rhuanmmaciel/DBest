package dsl.entities;

import entities.cells.OperationCell;
import enums.OperationArity;
import enums.OperationType;

import java.util.ArrayList;
import java.util.List;

public abstract sealed class OperationExpression extends dsl.entities.Expression<OperationCell> permits UnaryExpression, BinaryExpression {

	private OperationType type;
	private OperationArity arity;
	private final List<String> arguments = new ArrayList<>();
	private dsl.entities.Expression<?> source;
	private OperationCell cell = null;
	
	public OperationExpression(String command) {
		super(command);
	}
	
	protected void setType(OperationType type) {
		
		this.type = type;
		setArity();
		
	}
	
	private void setArity() {
		
		if(type == null) {
			arity = null;
			return;
		}

		arity = type.arity;
		
	}

	protected void setArguments(List<String> arguments) {
		
		this.arguments.addAll(arguments.stream().map(String::strip).toList());
		
	}
	
	protected void setSource(dsl.entities.Expression<?> source) {
		this.source = source;
	}
	
	@Override
	public void setCell(OperationCell cell) {
		
		if(this.cell == null) this.cell = cell;
		
	}
	
	public OperationType getType() {
		return type;
	}
	
	public OperationArity getArity() {
		return arity;
	}
	
	public List<String> getArguments() {
		return List.copyOf(arguments);
	}
	
	public Expression<?> getSource() {
		return source;
	}
	
	@Override
	public OperationCell getCell() {
		return cell;
	}
	
}
