package ibd.exceptions;

import java.util.ArrayList;

public class DataBaseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4411376436433609366L;

	private ArrayList<String> validations=new ArrayList<String>();
	private String locale;

	public DataBaseException(String locale,String message) {
		super(message);
		this.locale=locale;
	}
	public DataBaseException(String locale,String message,String validator) {
		super(message);
		this.locale=locale;
		validations.add(validator);
	}

	public void addLocationToPath(String path) {
		locale=path+"->"+locale;
	}
	
	public void addValidation(String str) {
		validations.add(str);
	}
}
