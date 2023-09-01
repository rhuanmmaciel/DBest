package exceptions;

public class InvalidCsvException extends Exception{

	private static final long serialVersionUID = 9057235844324768504L;

	public InvalidCsvException() {
		
	}
	
	public InvalidCsvException(String txt) {
		super(txt);
	}
	
}
