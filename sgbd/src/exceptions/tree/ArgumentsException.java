package exceptions.tree;

public class ArgumentsException extends TreeException{

	public ArgumentsException(String txt) {
		super(txt);
	}

	@Override
	public ExceptionType type() {
		return ExceptionType.ARGUMENTS;
	}
	
}
