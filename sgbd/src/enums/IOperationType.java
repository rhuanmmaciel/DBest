package enums;

public interface IOperationType {
	
	String getDisplayName();
	String getSymbol();
	String getDisplayNameAndSymbol();
	String getOperationName();
	String getDslOperation();
	OperationArity getArity();
    
}
