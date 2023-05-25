package enums;

import gui.frames.forms.operations.IOperator;

public interface IOperationType {
	
	String getDisplayName();
	String getSymbol();
	String getDisplayNameAndSymbol();
	String getOperationName();
	String getDslOperation();
	OperationArity getArity();
	Class<? extends IOperator> getForm();
    
}
