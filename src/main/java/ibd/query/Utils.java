/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query;

import ibd.query.binaryop.BinaryOperation;
import ibd.query.sourceop.SourceOperation;
import ibd.query.unaryop.UnaryOperation;


/**
 *
 * @author Sergio
 */
public class Utils {

    public static void toString(Operation op, int tab){

        for (int i = 0; i < tab; i++) {
            System.out.print("  ");
        }
        //System.out.println(op.getClass().toString());
        System.out.println(op.toString());
        
        if (op instanceof UnaryOperation)
        {
            UnaryOperation uop = (UnaryOperation) op;
            toString(uop.getChildOperation(), tab+4);
        }
        else if (op instanceof BinaryOperation){
            BinaryOperation bop = (BinaryOperation) op;
            toString(bop.getLeftOperation(), tab+4);
            toString(bop.getRightOperation(), tab+4);
        }
        else if (op instanceof SourceOperation){
            SourceOperation sop = (SourceOperation) op;
            
        }
        
    }
    
}
