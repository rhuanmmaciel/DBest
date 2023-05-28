package exceptions;

public abstract class TreeException extends Exception{

	private static final long serialVersionUID = 3453003137947098627L;
	
	public enum ExceptionType{
		ARGUMENTS, PARENTS_ERROR, PARENTS_AMOUNT
	}
	
    public TreeException(String txt) {
        super(txt);
    }

    public abstract ExceptionType type();
    
}
