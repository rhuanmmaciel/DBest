package ibd.query.optimizer.sort;

import ibd.query.Operation;
import ibd.query.Utils;
import ibd.query.binaryop.BinaryOperation;
import ibd.query.binaryop.join.MergeJoin;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.unaryop.UnaryOperation;
import ibd.query.unaryop.sort.external.ExternalSort;

public class DanielPereiraQueryOptimizer implements QueryOptimizer {
    @Override
    public Operation transform(Operation op) throws Exception {
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        
        if (op instanceof BinaryOperation) {
            BinaryOperation bop = (BinaryOperation) op;
            Operation leftOperation = this.transform(bop.getLeftOperation());
            Operation rightOperation = this.transform(bop.getRightOperation());
            if (this.isOrderedOperation(leftOperation) || this.isOrderedOperation(rightOperation)) {
                if (!this.isOrderedOperation(rightOperation)) rightOperation = new ExternalSort(rightOperation);
                if (!this.isOrderedOperation(leftOperation)) leftOperation = new ExternalSort(leftOperation);
                if (!this.isOrderedOperation(bop)) {
                    Operation parent = bop.getParentOperation();
                    bop = new MergeJoin(leftOperation, rightOperation, terms);
                    bop.setParentOperation(parent);
                }

                leftOperation.setParentOperation(bop);
                rightOperation.setParentOperation(bop);
                bop.setLeftOperation(leftOperation);
                bop.setRightOperation(rightOperation);

                return bop;
            }
        } else if (op instanceof UnaryOperation) {
            UnaryOperation uop = (UnaryOperation) op;
            uop.setChildOperation(this.transform(uop.getChildOperation()));
        }

        return op;
    }

    private boolean isOrderedOperation(Operation op) {
        return op instanceof ExternalSort || op instanceof MergeJoin;
    }
}
