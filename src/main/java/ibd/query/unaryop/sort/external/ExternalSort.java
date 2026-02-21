/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop.sort.external;

import ibd.query.Operation;
import ibd.query.Tuple;
import ibd.query.lookup.LookupFilter;
import ibd.query.unaryop.UnaryOperation;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Sergio
 */
public class ExternalSort extends UnaryOperation{

    ExternalMergeSort sorter;
    InputBucket sortedInputBucket;
    
    int inputBucketSize;
    int outputBucketSize;
    
    String sourceName;
    int tupleIndex = -1;
    boolean opened = false;
    
    public ExternalSort(Operation op) throws Exception{
        this(op, null, 10, 10);
    }
    
    public ExternalSort(Operation op, String sourceName, int inputBucketSize, int outputBucketSize) throws Exception{
        super(op);
        this.sourceName = sourceName;
        //sorter = new ExternalMergeSort(inputBucketSize, outputBucketSize);
        //sorter = new RicardoExternalMergeSort(inputBucketSize, outputBucketSize);
        this.inputBucketSize = inputBucketSize;
        this.outputBucketSize = outputBucketSize;
    }
    
    private void findSourceIndex() throws Exception{
//     if (sourceName == null) {
//            tupleIndex = 0;
//        } else {
//        String[] sources = getSources();
//        for (int i = 0; i < sources.length; i++) {
//            if (sources[i].equals(sourceName)){
//                tupleIndex = i;
//                break;
//            }
//            
//        }
//     }
//        if (tupleIndex==-1)
//            throw new Exception("source not found");
    }
    
    
    @Override
    public void prepare() throws Exception {
        super.prepare();
        
        findSourceIndex();
        
        //if (opened) return;
        sorter = new ExternalMergeSort(inputBucketSize, outputBucketSize);
        sorter.setTupleIndex(tupleIndex);
//        while (op.hasNext())
//        {
//            Tuple tuple = op.next();
//            sorter.addTuple(tuple);
//        }
        sortedInputBucket = sorter.sort();
        
        opened = true;
        
    }
    
    
    
    
    @Override
     public String toString(){
         return "ExternalSort";
     }

    @Override
    protected Iterator<Tuple> lookUp_(List<Tuple> processedTuples,  boolean withFilterDelegation) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
