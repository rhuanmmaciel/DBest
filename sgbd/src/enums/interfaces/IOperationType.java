package enums.interfaces;

import enums.OperationArity;
import gui.frames.forms.operations.FormFrameOperation;
import gui.frames.forms.operations.IOperator;

public interface IOperationType {
	
	String getDisplayName();
	String getSymbol();
	String getDisplayNameAndSymbol();
	String getOperationName();
	String getDslOperation();
	OperationArity getArity();
	Class<? extends FormFrameOperation> getForm();
	Class<? extends IOperator> getOperator();
    
}
