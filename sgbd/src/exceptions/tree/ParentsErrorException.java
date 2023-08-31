package exceptions.tree;

public class ParentsErrorException extends ParentsException{

	public ParentsErrorException(String txt) {
		super(txt);
	}

	@Override
	public ExceptionType type() {
		return ExceptionType.PARENTS_ERROR;
	}

}
