package exceptions.tree;

public class ParentsAmountException extends TreeException{

	public ParentsAmountException(String txt) {
			super(txt);
		}

	@Override
	public ExceptionType type() {
		return ExceptionType.PARENTS_ERROR;
	}

}
