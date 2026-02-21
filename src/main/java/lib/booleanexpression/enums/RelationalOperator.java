package lib.booleanexpression.enums;

public enum RelationalOperator {
	
    GREATER_THAN_OR_EQUAL(">=", "≥"),
    LESS_THAN_OR_EQUAL("<=", "≤"),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    NOT_EQUAL("!=", "≠"),
    EQUAL("==", "="),
    IS_NOT("is not", "IS NOT"),
    IS("is", "IS");
    
    RelationalOperator(String ...symbol){
    	
    	this.symbols = symbol;
    	
    }
    
    public static RelationalOperator getOperator(String symbol) {
    	
    	return switch(symbol) {
    		
    	case "<" -> LESS_THAN;
    	case ">" -> GREATER_THAN;
    	case ">=", "≥" -> GREATER_THAN_OR_EQUAL;
    	case "<=", "≤" -> LESS_THAN_OR_EQUAL;
    	case "=", "==" -> EQUAL;
    	case "!=", "≠" -> NOT_EQUAL;
    	case "is", "IS" -> IS;
    	case "is not", "IS NOT" -> IS_NOT;
    	
    	default -> throw new IllegalArgumentException("this is not a valid operator");
    	
    	};
    	
    }
    
    public final String[] symbols;
    
}
