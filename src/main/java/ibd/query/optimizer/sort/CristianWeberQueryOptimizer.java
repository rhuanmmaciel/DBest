package ibd.query.optimizer.sort;

import ibd.query.Operation;
import ibd.query.binaryop.join.MergeJoin;
import ibd.query.unaryop.UnaryOperation;
import ibd.query.binaryop.BinaryOperation;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.unaryop.sort.external.ExternalSort;

public class CristianWeberQueryOptimizer implements QueryOptimizer{

    public Operation transform(Operation op)throws Exception{

        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        
        if (op instanceof UnaryOperation && !this.isOrderedOperation(op))
        {
            UnaryOperation uop = (UnaryOperation) op;
            uop.setChildOperation(this.transform(uop.getChildOperation()));
            return uop;
        }
        else if (op instanceof BinaryOperation && !this.isOrderedOperation(op))
        {
            BinaryOperation bop = (BinaryOperation) op;
            bop.setLeftOperation(this.transform(bop.getLeftOperation()));
            bop.setRightOperation(this.transform(bop.getRightOperation()));
            if(!this.isOrderedOperation(bop.getLeftOperation()) && !this.isOrderedOperation(bop.getRightOperation()))
            {
                return bop;
            }else if(this.isOrderedOperation(bop.getLeftOperation()) && this.isOrderedOperation(bop.getRightOperation()))
            {
                return new MergeJoin(bop.getLeftOperation(), bop.getRightOperation(), terms);
            }else
            {
                if (!this.isOrderedOperation(bop.getLeftOperation()) && this.isOrderedOperation(bop.getRightOperation()))
                {
                    ExternalSort externalSort = new ExternalSort(bop.getLeftOperation());
                    bop.setLeftOperation(externalSort);
                }else if(this.isOrderedOperation(bop.getLeftOperation()) && !this.isOrderedOperation(bop.getRightOperation()))
                {
                    ExternalSort externalSort = new ExternalSort(bop.getRightOperation());
                    bop.setRightOperation(externalSort);
                }
                return this.transform(bop);
            }
        }
        return op;
    }

    private boolean isOrderedOperation(Operation op){
        return op instanceof ExternalSort || op instanceof MergeJoin;
    }
}




