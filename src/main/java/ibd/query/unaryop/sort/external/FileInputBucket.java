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
public class FileInputBucket implements InputBucket {

    int totalNumberOfTuplesRead = 0;
    long currentNumberOfTuplesRead = 0;
    int currentIndex = -1;
    int bucketSize = -1;
    Tuple tuples[];

    TupleBucketIO bucketIO;
    
    boolean closed = false;

    public FileInputBucket(String folder, String name, int bucketSize) throws Exception {

        bucketIO = new TupleBucketIO(folder, name, false);
        bucketIO.open();
        this.bucketSize = bucketSize;
        tuples = new Tuple[bucketSize];
        loadBucket();
    }

    @Override
    public boolean hasNext() throws Exception {
        if (closed)
            throw new Exception("file is closed");
        
        if (totalNumberOfTuplesRead >= bucketIO.getBucketSize()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Tuple next() throws Exception {

        if (closed)
            throw new Exception("file is closed");
        
        if (currentIndex + 1 < currentNumberOfTuplesRead) {
            totalNumberOfTuplesRead++;
            return tuples[++currentIndex];
        } else {
            loadBucket();
            totalNumberOfTuplesRead++;
            return tuples[++currentIndex];
        }

    }

    @Override
    public void close() throws IOException{
        bucketIO.close();
        closed = true;
    }
    
    private void loadBucket() throws Exception {

        currentNumberOfTuplesRead = bucketSize;
        if (totalNumberOfTuplesRead + currentNumberOfTuplesRead > bucketIO.getBucketSize()) {
            currentNumberOfTuplesRead = bucketIO.getBucketSize() - totalNumberOfTuplesRead;
        }
        for (int i = 0; i < currentNumberOfTuplesRead; i++) {
            tuples[i] = bucketIO.readTuple();
            //totalNumberOfTuplesRead++;

        }
        currentIndex = -1;

    }

}
