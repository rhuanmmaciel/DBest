package ibd.table.boolean_expression;

import ibd.table.ComparisonTypes;
import ibd.table.prototype.DataRow;

public class ExpressionSolverJosianeAggens implements ExpressionSolver{


    @Override
    public boolean solve(Expression exp, DataRow row) {
        if(exp instanceof SingleExpression){
            SingleExpression e = (SingleExpression) exp;
            return ComparisonTypes.match(row.getValue(e.colName), e.value, e.comparisonType);
        }else if(exp instanceof CompositeExpression){
            CompositeExpression e = (CompositeExpression) exp;
            if(e.boolean_conector == CompositeExpression.OR){
                boolean b = false;
                for(Expression i : e.expressions){
                    if(this.solve(i, row)){
                        b = true;
                    }
                }
                return b;
            }
            if(e.boolean_conector == CompositeExpression.AND){
                boolean b = true;
                for(Expression i : e.expressions){
                    if(!this.solve(i, row)){
                        b = false;
                    }
                }
                return b;
            }
            return false;
        }else if(exp instanceof NegationExpression){
            NegationExpression ne = (NegationExpression) exp;
            return !this.solve(ne.getExpression(), row);
        }else{
            return false;
        }
    }
}
