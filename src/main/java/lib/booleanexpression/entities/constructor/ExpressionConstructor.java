package lib.booleanexpression.entities.constructor;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import lib.booleanexpression.entities.expressions.BooleanExpression;
import lib.booleanexpression.entities.expressions.LogicalExpression;
import lib.booleanexpression.enums.LogicalOperator;

public class ExpressionConstructor {

    private final ArrayList<Map.Entry<String,BooleanExpression>> queue = new ArrayList<>();

    public ExpressionConstructor(){
    }


    public void and(BooleanExpression e){
        queue.add(Map.entry("and",e));
    }

    public void or(BooleanExpression e){
        queue.add(Map.entry("or",e));
    }
    public void and(CallbackExpressionConstructor callback){
        ExpressionConstructor ec = new ExpressionConstructor();
        callback.handle(ec);
        queue.add(Map.entry("and",ec.build()));
    }

    public void or(CallbackExpressionConstructor callback){
        ExpressionConstructor ec = new ExpressionConstructor();
        callback.handle(ec);
        queue.add(Map.entry("or",ec.build()));
    }



    public BooleanExpression build(){
        ArrayList<BooleanExpression> allAnd = new ArrayList<>();
        String current = null;
        ArrayList<BooleanExpression> list = new ArrayList<>();
        for (Map.Entry<String,BooleanExpression> e:
             queue) {
            if(current==null){
                current = e.getKey();
            }else if(!current.equals(e.getKey())){
                if(Objects.equals(e.getKey(), "and")){
                    BooleanExpression last = list.remove(list.size()-1);
                    allAnd.addAll(list);
                    list = new ArrayList<>();
                    list.add(last);
                }else{
                    allAnd.add(new LogicalExpression(LogicalOperator.AND,list));
                    list = new ArrayList<>();
                }
                current = e.getKey();
            }
            list.add(e.getValue());
        }
        if(current!=null && !current.equals("and"))
            allAnd.add(new LogicalExpression(LogicalOperator.OR,list));
        else
            allAnd.addAll(list);
        if(allAnd.size()==1)
            return allAnd.get(0);
        return new LogicalExpression(LogicalOperator.AND,allAnd);
    }

}
