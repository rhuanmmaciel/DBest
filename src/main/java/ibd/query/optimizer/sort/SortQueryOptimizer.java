/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.optimizer.sort;

import ibd.query.Operation;
import ibd.query.binaryop.BinaryOperation;
import ibd.query.binaryop.join.MergeJoin;
import ibd.query.binaryop.join.NestedLoopJoin;
import ibd.query.binaryop.join.JoinPredicate;
import ibd.query.sourceop.SourceOperation;
import ibd.query.unaryop.UnaryOperation;
import ibd.query.unaryop.sort.external.ExternalSort;

/**
 *
 * @author Sergio
 */
public class SortQueryOptimizer implements QueryOptimizer{
    
    Operation root;
    
    @Override
    public Operation transform(Operation op) throws Exception{
    root = op;
    transform_(op);
    return root;
    }

    public boolean transform_(Operation op) throws Exception{
        
        JoinPredicate terms = new JoinPredicate();
        terms.addTerm("id", "id");
        
        if (op instanceof MergeJoin){
            BinaryOperation bop = (BinaryOperation) op;
            transform_(bop.getLeftOperation());
            transform_(bop.getRightOperation());
            return true;
        
        }
        else if (op instanceof ExternalSort){
            UnaryOperation uop = (UnaryOperation) op;
            transform_(uop.getChildOperation());
            return true;
        }
        
        if (op instanceof NestedLoopJoin){
            NestedLoopJoin nljop = (NestedLoopJoin) op;
            Operation leftOp = nljop.getLeftOperation();
            Operation rightOp = nljop.getRightOperation();
            boolean leftok = transform_(leftOp);
            boolean rightok = transform_(rightOp);
            
            leftOp = nljop.getLeftOperation();
            rightOp = nljop.getRightOperation();            
            
            if (leftok || rightok){
                MergeJoin mergeJoin = null;
                if (leftok && rightok)
                    mergeJoin = new MergeJoin(leftOp,rightOp, terms);
                else if (leftok){
                    mergeJoin = new MergeJoin(leftOp, new ExternalSort(rightOp), terms);
                }
                else if (rightok){
                    mergeJoin = new MergeJoin(new ExternalSort(leftOp), rightOp, terms);
                }
                
                replaceOperation(nljop, mergeJoin);
                return true;
            }
            else return false;
            }
            if (op instanceof UnaryOperation) {
            UnaryOperation uop = (UnaryOperation) op;
            return transform_(uop.getChildOperation());
        }
        else if (op instanceof SourceOperation) {
            return false;
        }
        
        return false;
        
    
    }
    
    private void replaceOperation(NestedLoopJoin operation, MergeJoin op){
        Operation parentOp = operation.getParentOperation();
        
        
        if (parentOp instanceof BinaryOperation){
            BinaryOperation bop = (BinaryOperation) parentOp;
            if (bop.getLeftOperation().equals(operation))
                bop.setLeftOperation(op);
            else bop.setRightOperation(op);
        }
        else if (parentOp instanceof UnaryOperation){
            UnaryOperation uop = (UnaryOperation) parentOp;
            uop.setChildOperation(op);
        }
        
        if (operation.equals(root))
            root = op;
    
    }
    
    
    
    

}
