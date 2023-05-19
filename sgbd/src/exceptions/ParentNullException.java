package exceptions;

public class ParentNullException extends RuntimeException {

	private static final long serialVersionUID = 4187858228269795125L;

	public ParentNullException() {
		super();
	}

	public ParentNullException(String message) {
		super(message);
	}

}
