package exceptions.tree;

public class ArgumentsException extends TreeException{

	private static final long serialVersionUID = 6944760171982147427L;

	public ArgumentsException(String txt) {
		super(txt);
	}

	@Override
	public ExceptionType type() {
		return ExceptionType.ARGUMENTS;
	}
	
}
