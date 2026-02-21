package lib.booleanexpression.enums;

public enum Type {

    NUMBER,
    TEXT,
    BOOLEAN;

    public static Type getType(Object obj){

        if(obj instanceof Number) return NUMBER;
        if(obj instanceof Boolean) return BOOLEAN;
        if(obj instanceof Character || obj instanceof String) return TEXT;

        throw new IllegalArgumentException("This type is not supported");

    }

}
