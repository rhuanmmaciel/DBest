package gui.frames.forms.data;

import java.util.ArrayList;
import java.util.List;

import net.datafaker.Faker;
import net.datafaker.providers.base.AbstractProvider;
import net.datafaker.providers.base.BaseProviders;

public class CustomProviders{
	
	public static class MyCustomFaker extends Faker {
	    public Char character() {
	        return getProvider(Char.class, Char::new, this);
	    }
	}

	
	public static class Char extends AbstractProvider<BaseProviders> {
		
	    private static final String[] UPPERCASE_LETTERS = new String[]{"A", "B", "C", "D", "E", "F", "G", "H",
	    															   "I", "J", "K", "L", "M", "N", "O", "P",
	    															   "Q", "R", "S", "T", "U", "V", "W", "X",
	    															   "Y", "Z"};
	    private static final String[] LOWERCASE_LETTERS = new String[] {"a", "b", "c", "d", "e", "f", "g", "h",
	    																"i", "j", "k", "l", "m", "n", "o", "p",
	    																"q", "r", "s", "t", "u", "v", "w", "x",
	    																"y", "z"};
	    private static final String[] SPECIAL_CHARACTER = new String[] {"!", "@", "#", "$", "%", "&", "*",
	    																"+", "-", "/", ":", ";", "<", "=",
	    																">", "?", "@", "^", "`", "{", "|",
	    																"}", "~"};
	    private static final String[] NUMBER = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
	    
	    public Char(BaseProviders faker) {
	        super(faker);
	    }

	    public String uppercaseLetter() {
	        return UPPERCASE_LETTERS[faker.random().nextInt(UPPERCASE_LETTERS.length)];
	    }
	    
	    public String lowercaseLetter() {
	    	return LOWERCASE_LETTERS[faker.random().nextInt(LOWERCASE_LETTERS.length)];
	    }
	    
	    public String specialChar() {
	    	return SPECIAL_CHARACTER[faker.random().nextInt(SPECIAL_CHARACTER.length)];
	    }
	    
	    public String numberChar() {
	    	return NUMBER[faker.random().nextInt(NUMBER.length)];
	    }
	    
	    public String anyChar(boolean upper, boolean lower, boolean special, boolean number) {

	    	List<String> options = new ArrayList<>();
	    	if(upper) options.add(uppercaseLetter());
	    	if(lower) options.add(lowercaseLetter());
	    	if(special) options.add(specialChar());
	    	if(number) options.add(numberChar());
	    	
	    	return options.get(faker.random().nextInt(options.size()));
	    	
	    }
	    

	    
	    
	}
	
}



