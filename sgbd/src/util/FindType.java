package util;

public class FindType {

	public static boolean isInt(String inf) {
		
		try {
			
			Integer.parseInt(inf);
			return true;
			
		}catch(NumberFormatException e) {
			
			return false;
			
		}
		
	}
	
	public static boolean isFloat(String inf) {
		
		try {
			
			Float.parseFloat(inf);
			return true;
			
		}catch(NumberFormatException e) {
			
			return false;
			
		}
		
	}
	
}
