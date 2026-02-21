/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.binaryop;

import ibd.query.Operation;
import ibd.query.ReferedDataSource;
import ibd.query.binaryop.conditional.Exists;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Defines the template of a binary operation. A binary operation accesses data
 * that comes from two operations (from the left and from the right) to perform
 * data transformation. The data transformation is usually based on comparisons between tuples 
 * @author Sergio
 */
public abstract class BinaryOperation extends Operation {

    /**
     * the left side operation
     */
    protected Operation leftOperation;

    /**
     * the right side operation
     */
    protected Operation rightOperation;

    /**
     *
     * @param leftOperation the left side operation
     * @param rightOperation the right side operation
     * @throws Exception
     */
    public BinaryOperation(Operation leftOperation, Operation rightOperation) throws Exception {
        super();
        setLeftOperation(leftOperation);
        setRightOperation(rightOperation);
        
        
    }
    
    public boolean useLeftSideLookups(){
        return false;
    }
    
   @Override
    public void prepare() throws Exception {
        
        //the preparation is propagated to the left and right operations
        getLeftOperation().prepare();
        getRightOperation().prepare();
        
        super.prepare();
    }
    
    @Override
    public final void setProcessedOperations() {
        super.setProcessedOperations();
        getLeftOperation().setProcessedOperations();
        getRightOperation().setProcessedOperations();
    }

    /**
     * sets the left side operation
     *
     * @param op
     */
    public final void setLeftOperation(Operation op) {
        leftOperation = op;
        op.setParentOperation(this);
        refreshChildOperations();
    }
    
    /**
     * sets the right side operation
     *
     * @param op
     */
    public final void setRightOperation(Operation op) {
        rightOperation = op;
        op.setParentOperation(this);
        refreshChildOperations();
    }
    
    private void refreshChildOperations(){
        childOperations.clear();
        if (leftOperation!=null)
            childOperations.add(leftOperation);
        if (rightOperation!=null)
            childOperations.add(rightOperation);
    }

    /**
     * gets the left side operation
     *
     * @return
     */
    public Operation getLeftOperation() {
        return leftOperation;
    }

    /**
     * gets the right side operation
     *
     * @return
     */
    public Operation getRightOperation() {
        return rightOperation;
    }

    @Override
    public void close() throws Exception {
        leftOperation.close();
        rightOperation.close();
    }
    
    @Override
    public Map<String, List<String>> getContentInfo() {
        return getStringListMap(leftOperation, rightOperation);
    }

    protected static Map<String, List<String>> getStringListMap(Operation left, Operation right) {
        Map<String, List<String>> lTable = left.getContentInfo();
        Map<String, List<String>> rTable = right.getContentInfo();
        for(Map.Entry<String,List<String>> a: lTable.entrySet()){
            if(rTable.containsKey(a.getKey())){
                a.setValue(Stream.concat(a.getValue().stream(),rTable.get(a.getKey()).stream()).collect(Collectors.toList()));
                rTable.remove(a.getKey());
            }
        }
        return Stream.concat(lTable.entrySet().stream(), rTable.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    
    @Override
    public void setConnectedDataSources() throws Exception {
        
        try {
        ReferedDataSource left[] = getLeftOperation().getExposedDataSources();
        ReferedDataSource right[] = getRightOperation().getExposedDataSources();
        connectedDataSources = new ReferedDataSource[left.length + right.length];
        int count = 0;
        for (int i = 0; i < left.length; i++) {
            connectedDataSources[count] = left[i];
            count++;
        }
        for (int i = 0; i < right.length; i++) {
            connectedDataSources[count] = right[i];
            count++;
        }
        } catch (Exception ex) {
            Logger.getLogger(Exists.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    

}
