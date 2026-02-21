package lib.booleanexpression.enums;

public enum Result {
    TRUE,
    FALSE,
    NOT_READY;

    public boolean val(){
        return (this == TRUE);
    }
    public Result invert(){
        if(this==NOT_READY)return this;
        if(this==TRUE)return Result.FALSE;
        return Result.TRUE;
    }
    public static Result evaluate(Boolean condition){

        if(condition) return TRUE;
        return FALSE;

    }

}
