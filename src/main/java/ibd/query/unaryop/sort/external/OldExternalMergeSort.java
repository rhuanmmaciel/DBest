/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop.sort.external;

import ibd.query.Tuple;

/**
 *
 * @author Sergio
 */
public class OldExternalMergeSort {

    int inputBucketSize;
    int outputBucketSize;

    int tupleIndex;

    int currentBucketIndex = 0;
    OutputBucket currentBucket;
    boolean externalSort = false;

    int totalNumberOfTuples = 0;

    String filePrefix;
    String folder = "c:\\teste\\ibd\\sort";

    public OldExternalMergeSort(int inputBucketSize, int outputBucketSize) throws Exception {
        long cur = System.currentTimeMillis();
        filePrefix = String.valueOf(cur);
        currentBucket = new SortedOutputBucket(folder, getBucketName(currentBucketIndex), outputBucketSize, tupleIndex);
        this.inputBucketSize = inputBucketSize;
        this.outputBucketSize = outputBucketSize;
    }
    

    public void addTuple(Tuple tuple) throws Exception {
        totalNumberOfTuples++;
        if (currentBucket.isFull()) {
            currentBucket.saveBucket();
            currentBucket.close();
            currentBucketIndex++;
            currentBucket = createSortedOutputBucket(currentBucketIndex);
            externalSort = true;
        }

        currentBucket.addTuple(tuple);
    }

    
    public InputBucket sort() throws Exception {
        if (externalSort) {
            currentBucket.saveBucket();
            OutputBucket.TOTAL_NUMBER_TUPLES_WROTE = 0;
            int finalBucketIndex = mergeAll(currentBucketIndex + 1);
            debug();
            return createInputBucket(finalBucketIndex);
        } else {
            return currentBucket.new MemoryBucket();
        }

    }

    
   private int mergeAll(int numberOfBuckets) throws Exception {

        int log = (int) (Math.log(numberOfBuckets) / Math.log(2));
        int numberOfBuckets2 = (int) Math.pow(2, log + 1);

        for (int i = numberOfBuckets; i < numberOfBuckets2; i++) {
            OutputBucket bucket = createOutputBucket(i);
            bucket.saveBucket();
            bucket.close();
        }

        int newBuckets = numberOfBuckets2;
        int oldBuckets = 0;
        while (numberOfBuckets2 != 1) {
            for (int i = 0; i < numberOfBuckets2 / 2; i++) {
                
                InputBucket bucket1 = createInputBucket(oldBuckets);
                InputBucket bucket2 = createInputBucket(oldBuckets+1);
                
                OutputBucket outputBucket = createOutputBucket(newBuckets);
                merge(bucket1, bucket2, outputBucket, tupleIndex);
                bucket1.close();
                bucket2.close();
                outputBucket.close();

                newBuckets++;
                oldBuckets += 2;
            }

            numberOfBuckets2 = numberOfBuckets2 / 2;
            //newBuckets += numberOfBuckets;
        }
        return newBuckets - 1;
    }


    public void merge(InputBucket bucket1, InputBucket bucket2, OutputBucket outputBucket, int tupleIndex) throws Exception {

        Tuple t1 = (bucket1.hasNext() ? bucket1.next() : null);
        Tuple t2 = (bucket2.hasNext() ? bucket2.next() : null);;

        // Loop while values remain in either list
        while (t1 != null || t2 != null) {

            // Choose list to pull value from
            if (t2 == null || (t1 != null && compare(t1, t2, tupleIndex) <= 0)) {

                // Add list1 value to result and fetch next value, if available
                outputBucket.addTuple(t1);
                t1 = (bucket1.hasNext() ? bucket1.next() : null);

            } else {

                // Add list2 value to result and fetch next value, if available
                outputBucket.addTuple(t2);
                t2 = (bucket2.hasNext() ? bucket2.next() : null);

            }
        }

        outputBucket.saveBucket();
    }
    
    public void setTupleIndex(int ti) {
        tupleIndex = ti;
    }

    private String getBucketName(int index) {
        return filePrefix + "bucket" + index;
    }
    
    private InputBucket createInputBucket(int index) throws Exception{
        return new FileInputBucket(folder, getBucketName(index), inputBucketSize);
    }

    private OutputBucket createOutputBucket(int index) throws Exception{
        return new OutputBucket(folder, getBucketName(index), outputBucketSize);
    }
    
    private OutputBucket createSortedOutputBucket(int index) throws Exception{
        return new SortedOutputBucket(folder, getBucketName(index), outputBucketSize, tupleIndex);
    }
    
    private void debug() {
//        System.out.println("--- START EXTERNAL MERGE --- ");
//        System.out.println("Total nummber of tuples " + totalNumberOfTuples);
//        System.out.println("Partition size " + outputBucketSize);
//        System.out.println("Partitions created during first phase " + (currentBucketIndex + 1));
//        int timesTupleWrote = OutputBucket.TOTAL_NUMBER_TUPLES_WROTE / totalNumberOfTuples;
//        System.out.println("times a tuple was wrote:" + timesTupleWrote);
//        System.out.println("--- END EXTERNAL MERGE --- ");
    }
    private int compare(Tuple t1, Tuple t2, int fieldIndex) {
//        return t1.sourceTuples[fieldIndex].record.getValue("id").compareTo(
//                t2.sourceTuples[fieldIndex].record.getValue("id"));
    return 0;
    }

}
