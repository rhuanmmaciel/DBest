/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop;

import ibd.query.Operation;
import ibd.query.UnpagedOperationIterator;
import ibd.query.ReferedDataSource;
import ibd.query.Tuple;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This operation renames one of the data sources that are part of the tuples returned by a child operation
 *
 * @author Sergio
 */
public class SourceRename extends UnaryOperation {

    String newAlias;
    //why a map, if a single alias is added? Better to change the map to a single variable
    HashMap<String, String> old2NewPairs = new HashMap();

    /**
     *
     * @param op the operation to be connected into this unary operation
     * @param oldAlias the alias to be replaced
     * @param newAlias the new alias
     * @throws Exception
     */
    public SourceRename(Operation op, String oldAlias, String newAlias) throws Exception {
        super(op);
        this.newAlias = newAlias;
        old2NewPairs.put(oldAlias, newAlias);
    }
    
    
    
    @Override
    public void setConnectedDataSources() throws Exception {
        
        
        ReferedDataSource s[] = childOperation.getExposedDataSources();
        connectedDataSources = new ReferedDataSource[s.length];
        
        int i = 0;
        //copies the array of data sources from the child operation
        //and replaces the alias of one of the data sources, if the old alias is found
        for (ReferedDataSource referedDataSource : s) {
            ReferedDataSource newDataSource = new ReferedDataSource();
            newDataSource.prototype = referedDataSource.prototype;
            String newAlias_ = old2NewPairs.get(referedDataSource.alias);
            if (newAlias_==null)
                newAlias_ = referedDataSource.alias;
            
            newDataSource.alias = newAlias_;
            connectedDataSources[i] = newDataSource;
            i++;
        }
        
    }
    
    @Override
    public void setExposedDataSources() throws Exception {

        dataSources = connectedDataSources;

    }
    
    @Override
    public Map<String, List<String>> getContentInfo() {
        Map<String,List<String>> newMap = new HashMap<String,List<String>>(); 
        Map<String,List<String>> oldMap = this.getChildOperation().getContentInfo();
        // why going for the child operation map?        
        //perhaps it would work by taking the data source array from this operation
        for (Map.Entry<String, List<String>> entry : oldMap.entrySet()) {
            String oldAlias_ = entry.getKey();
            String newAlias_ = old2NewPairs.get(oldAlias_);
            if (newAlias_==null)
                newAlias_ = oldAlias_;
            newMap.put(newAlias_, entry.getValue());
            
        }
        return newMap;
    }

    @Override
    public Iterator<Tuple> lookUp_(List<Tuple> processedTuples, boolean withFilterDelegation) {
        return new SourceRenameIterator(processedTuples, withFilterDelegation);
    }

    

    @Override
    public String toString() {
        return "Source Rename:" + newAlias;
    }

    /**
     * this class produces resulting tuples by removing duplicates from the
     * child operation
     */
    private class SourceRenameIterator extends UnpagedOperationIterator {

        //the iterator over the child operation
        Iterator<Tuple> tuples;

        
        public SourceRenameIterator(List<Tuple> processedTuples, boolean withFilterDelegation) {
            super(processedTuples, withFilterDelegation, getDelegatedFilters());
            tuples = childOperation.lookUp(processedTuples, false);//accesses all tuples produced by the child operation 
        }

        @Override
        protected Tuple findNextTuple() {
            while (tuples.hasNext()) {
                Tuple tp = tuples.next();
                return tp;

            }
            return null;
        }


    }
}
