package lib.booleanexpression.entities.expressions;

public abstract class BooleanExpression {

    private final boolean booleanValue;
    public BooleanExpression(boolean booleanValue){
        this.booleanValue = booleanValue;
    }

    //public abstract Result solve(Tuple t);

    public boolean isFalse(){
        return !booleanValue;
    }

}
