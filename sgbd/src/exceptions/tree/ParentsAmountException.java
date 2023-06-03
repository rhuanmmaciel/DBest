package exceptions.tree;

public class ParentsAmountException extends TreeException{

	private static final long serialVersionUID = 7046253632577090944L;

	public ParentsAmountException(String txt) {
			super(txt);
		}

	@Override
	public ExceptionType type() {
		return ExceptionType.PARENTS_ERROR;
	}

}
