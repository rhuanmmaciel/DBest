/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop.sort.external;

import ibd.query.Tuple;
import java.io.IOException;

/**
 *
 * @author Sergio
 */
public class OutputBucket {

    int bucketSize = 0;

    Tuple tuples[];
    int currentIndex = 0;

    TupleBucketIO tupleIO;

    public static int TOTAL_NUMBER_TUPLES_WROTE = 0;
    
    boolean closed = false;

    public OutputBucket(String folder, String name, int bucketSize) throws Exception {

        tupleIO = new TupleBucketIO(folder, name, true);
        this.bucketSize = bucketSize;
        tuples = new Tuple[bucketSize];
        tupleIO.open();
    }
    
    public void close() throws IOException{
        tupleIO.close();
        closed = true;
    }

    class MemoryBucket implements InputBucket {

        int currentReadIndex = 0;

        @Override
        public boolean hasNext() {
            if (currentReadIndex >= currentIndex) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public Tuple next() throws Exception {

            return tuples[currentReadIndex++];

        }

        @Override
        public void close() throws Exception {
        }
    }

    public void addTuple(Tuple tuple) throws Exception {
        
        if (closed)
            throw new Exception("file is closed");
        
        if (currentIndex >= bucketSize) {
            saveBucket();
        }
        tuples[currentIndex] = tuple;
        currentIndex++;
    }


    public boolean isFull() {
        return currentIndex == tuples.length;
    }

    public void saveBucket() throws Exception {

        if (closed)
            throw new Exception("file is closed");
        
        for (int i = 0; i < currentIndex; i++) {
            Tuple tuple = tuples[i];
            tupleIO.saveTuple(tuple);

        }

        TOTAL_NUMBER_TUPLES_WROTE += currentIndex;
        currentIndex = 0;

        tupleIO.updateFile(tuples[0]);

    }

}
