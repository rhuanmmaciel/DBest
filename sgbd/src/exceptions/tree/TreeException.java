package exceptions.tree;

public abstract class TreeException extends Exception{

	public enum ExceptionType{
		ARGUMENTS, PARENTS_ERROR, PARENTS_AMOUNT
	}
	
    public TreeException(String txt) {
        super(txt);
    }

    public abstract ExceptionType type();
    
}
