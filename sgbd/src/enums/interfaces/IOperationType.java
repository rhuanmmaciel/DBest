package enums.interfaces;

import entities.Action.CreateOperationAction;
import enums.OperationArity;
import gui.frames.forms.operations.IFormFrameOperation;
import operations.IOperator;

public interface IOperationType {
	
	String getDisplayName();
	String getSymbol();
	String getDisplayNameAndSymbol();
	String getOperationName();
	String getDslOperation();
	OperationArity getArity();
	Class<? extends IFormFrameOperation> getForm();
	Class<? extends IOperator> getOperator();
	CreateOperationAction getAction();
    
}
