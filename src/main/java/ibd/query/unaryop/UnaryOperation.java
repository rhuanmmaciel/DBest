/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.Operation;
import ibd.query.ReferedDataSource;
import java.util.List;
import java.util.Map;

/**
 * An unary operation accesses data that comes from a child operation to perform
 * data transformation.
 *
 * @author Sergio
 */
public abstract class UnaryOperation extends Operation {

    /**
     * the child operation.
     */
    protected Operation childOperation;

    /**
     *
     * @param childOperation the child operation
     * @throws Exception
     */
    public UnaryOperation(Operation childOperation) throws Exception {
        setChildOperation(childOperation);
    }

    /**
     * sets the child operation
     *
     * @param childOperation the child operation
     */
    public final void setChildOperation(Operation childOperation) {
        this.childOperation = childOperation;
        childOperations.add(childOperation);
        childOperation.setParentOperation(this);
    }

    /**
     *
     * @return the child operation
     */
    public Operation getChildOperation() {
        return childOperation;
    }

    @Override
    public void prepare() throws Exception {
        //the preparation is propagated to the child operation
        childOperation.prepare();
        
        super.prepare();
    }

    @Override
    public void close() throws Exception {
        childOperation.close();
    }

    
    @Override
    public void setConnectedDataSources() throws Exception {
        ReferedDataSource s[] = childOperation.getExposedDataSources();
        connectedDataSources = new ReferedDataSource[s.length];
        System.arraycopy(s, 0, connectedDataSources, 0, s.length);

    }
    
    @Override
    public void setExposedDataSources() throws Exception {
        ReferedDataSource s[] = childOperation.getExposedDataSources();
        dataSources = new ReferedDataSource[s.length];
        System.arraycopy(s, 0, dataSources, 0, s.length);

    }
    
    @Override
    public final void setProcessedOperations() {
        super.setProcessedOperations();
        childOperation.setProcessedOperations();
    }
    
    @Override
    public Map<String, List<String>> getContentInfo() {
        return childOperation.getContentInfo();
    }

}
