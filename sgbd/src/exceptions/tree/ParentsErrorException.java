package exceptions.tree;

public class ParentsErrorException extends ParentsException{

	private static final long serialVersionUID = 7046253632577090944L;

	public ParentsErrorException(String txt) {
		super(txt);
	}

	@Override
	public ExceptionType type() {
		return ExceptionType.PARENTS_ERROR;
	}

}
