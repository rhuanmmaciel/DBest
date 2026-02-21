package ibd.query.optimizer.sort;

import ibd.query.Operation;
import ibd.query.binaryop.BinaryOperation;
import ibd.query.binaryop.join.MergeJoin;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.sourceop.SourceOperation;
import ibd.query.unaryop.UnaryOperation;
import ibd.query.unaryop.sort.external.ExternalSort;

public class JulianoPasaQueryOptimizer implements QueryOptimizer {
    public Operation transform(Operation op) throws Exception{
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        
        if (op instanceof SourceOperation || isOrderedOp(op)) return op;

        else if (op instanceof UnaryOperation){
            ((UnaryOperation) op).setChildOperation(transform(((UnaryOperation) op).getChildOperation()));
            return op;
        }

        else {
            Operation leftOp = transform(((BinaryOperation) op).getLeftOperation());
            Operation rightOp = transform(((BinaryOperation) op).getRightOperation());

            if (!(isOrderedOp(leftOp) || isOrderedOp(rightOp))) return op; // Nenhum dos lados esta ordenado, mantem o nested loop join

            ExternalSort externalSort = new ExternalSort(leftOp); // Operacao qualquer
            MergeJoin mergeJoin = new MergeJoin(leftOp, rightOp, terms);

            // A operacao filha a esquerda nao estava ordenada
            if (!isOrderedOp(leftOp)) {
                externalSort.setChildOperation(leftOp);
                mergeJoin.setLeftOperation(externalSort);
            }
            // A operacao filha a direita nao estava ordenada
            else if (!isOrderedOp(rightOp)){
                externalSort.setChildOperation(rightOp);
                mergeJoin.setRightOperation(externalSort);
            }

            return mergeJoin;
        }
    }

    private boolean isOrderedOp(Operation op){
        if (op instanceof ExternalSort || op instanceof MergeJoin) return true;

        if (op instanceof UnaryOperation) return isOrderedOp(((UnaryOperation) op).getChildOperation());

        return false;
    }
}
