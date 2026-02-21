/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.unaryop.sort.external;

import ibd.query.Tuple;

import java.util.LinkedList;

/**
 *
 * @author Sergio
 */
public class ExternalMergeSort {

    int inputBucketSize;
    int outputBucketSize;

    int tupleIndex;

    int currentBucketIndex = 0;
    OutputBucket currentBucket;
    boolean externalSort = false;

    int totalNumberOfTuples = 0;

    String filePrefix;
    String folder = "c:\\teste\\ibd\\sort";

    public ExternalMergeSort(int inputBucketSize, int outputBucketSize) throws Exception {
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
        //System.out.println("External sort: "+externalSort);
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
        // The idea here is to have a fifo queue-like structure
        // where we merge the buckets at the beginning and when
        // we do this, we create a new bucket and add it to the end of
        // the queue. The process repeats until we have exhausted the queue.
        // This also will work regardless we have even or odd buckets :).
        LinkedList<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numberOfBuckets; i++) {
            queue.add(i); // Que "bucket" will be only the index, because it is what we need
        }

        // When the queue size is 1, it means
        // the we do not have anyone to merge
        // therefore the last created bucket is
        // already the merged one.
        while (queue.size() > 1) {
            this.merge(
                    createInputBucket(queue.pop()),
                    createInputBucket(queue.pop()),
                    createOutputBucket(numberOfBuckets),
                    0
            );
            queue.add(numberOfBuckets++); // Adds first and then increment
        }

        // we return the same original implementation because now it will be correct
        return numberOfBuckets - 1;
    }


    public void merge(InputBucket bucket1, InputBucket bucket2, OutputBucket outputBucket, int tupleIndex) throws Exception {
        Tuple t1, t2;
        t1 = t2 = null;
        while(bucket1.hasNext() || bucket2.hasNext() || t1 != null || t2 != null) {
            if (t1 == null && bucket1.hasNext()) t1 = bucket1.next();
            if (t2 == null && bucket2.hasNext()) t2 = bucket2.next();
            if (t2 == null || (t1 != null && compare(t1, t2, tupleIndex) <= 0)) {
                outputBucket.addTuple(t1);
                t1 = null; // Clear the head of comparation allowing more tuples of this bucket to come in
            } else {
                outputBucket.addTuple(t2);
                t2 = null; // Clear the head of comparation allowing more tuples of this bucket to come in
            }
        }

        outputBucket.saveBucket(); // Flush to disk
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
