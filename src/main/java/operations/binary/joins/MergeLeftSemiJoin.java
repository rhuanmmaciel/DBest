package operations.binary.joins;

import ibd.query.Operation;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.lookup.ExpressionConverter;
import java.util.logging.Level;
import java.util.logging.Logger;
import lib.booleanexpression.entities.expressions.BooleanExpression;

public class MergeLeftSemiJoin extends JoinOperators {

    @Override
    public Operation createJoinOperator(Operation operator1, Operation operator2, BooleanExpression booleanExpression) {
        try {
            JoinPredicate joinPredicate = ExpressionConverter.convert2JoinPredicate(booleanExpression);
            return new ibd.query.binaryop.join.semi.MergeLeftSemiJoin(operator1, operator2, joinPredicate);
        } catch (Exception ex) {
            Logger.getLogger(Join.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Override
    public Operation createJoinOperator(Operation operator1, Operation operator2, JoinPredicate joinPredicate) {
        try {
            return new ibd.query.binaryop.join.semi.MergeLeftSemiJoin(operator1, operator2, joinPredicate);
        } catch (Exception ex) {
            Logger.getLogger(Join.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
