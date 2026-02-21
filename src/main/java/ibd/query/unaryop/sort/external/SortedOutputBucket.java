/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop.sort.external;

import java.util.Arrays;

/**
 *
 * @author Sergio
 */
public class SortedOutputBucket extends OutputBucket{
    
    int sortIndex = -1;
    
    public SortedOutputBucket(String folder, String name, int outputBucketSize, int sortIndex) throws Exception{
        super(folder, name, outputBucketSize);
        this.sortIndex = sortIndex;
    }


    @Override
    public void saveBucket() throws Exception{
    
        Arrays.sort(tuples,  0, currentIndex, new TupleComparator(sortIndex));
        
        super.saveBucket();
        
    }
    
}
