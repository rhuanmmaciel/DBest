/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author Sergio
 */
/**
 * This class represents the leaf nodes within the B+ tree that hold dictionary
 * pairs. The leaf node has no children. The leaf node has a minimum and maximum
 * number of dictionary pairs it can hold, as specified by m, the max degree of
 * the B+ tree. The leaf nodes form a doubly linked list that, i.e. each leaf
 * node has a left and right sibling
 */
public class LeafNode extends Node {

    int maxNumPairs;
    int minNumPairs;
    int numPairs;

    BPlusTree tree;

    LeafNode leftSibling;
    LeafNode rightSibling;
    int leftSiblingID = -1;
    int rightSiblingID = -1;
    //int parentID;

    DictionaryPair[] dictionary;

    public LeafNode(BPlusTree tree) {
        this.tree = tree;
    }

    /**
     * Constructor
     *
     * @param m: order of B+ tree that is used to calculate maxNumPairs and
     * minNumPairs
     * @param dp: first dictionary pair insert into new node
     */
    public LeafNode(int m, DictionaryPair dp, BPlusTree tree) {
        this.maxNumPairs = m - 1;
        //this.minNumPairs = (int) (Math.ceil(m / 2) - 1);
        setMinNumPairs();
        this.dictionary = new DictionaryPair[m];
        this.numPairs = 0;
        this.tree = tree;
        this.insert(dp);
    }

    /**
     * Constructor
     *
     * @param dps: list of DictionaryPair objects to be immediately inserted
     * into new LeafNode object
     * @param m: order of B+ tree that is used to calculate maxNumPairs and
     * minNumPairs
     * @param parent: parent of newly created child LeafNode
     */
    public LeafNode(int m, DictionaryPair[] dps, InternalNode parent) {
        this.maxNumPairs = m - 1;
        //this.minNumPairs = (int) (Math.ceil(m / 2) - 1);
        setMinNumPairs();
        this.dictionary = dps;
        this.numPairs = Utils.linearNullSearch(dps);
        this.setParentNode(parent);
        //this.parentID = parent.getPageID();
    }

    private void setMinNumPairs() {
        this.minNumPairs = (int) (Math.ceil((maxNumPairs + 1) / 2) - 1);
    }

    public void print() {
        StringBuffer buf = new StringBuffer();
        buf.append("maxNumPairs " + maxNumPairs + ","
                + "minNumPairs " + minNumPairs + ","
                + "numPairs " + numPairs + ","
                + "leftSiblingID " + leftSiblingID + ","
                + "rightSiblingID " + rightSiblingID + ","
                + "parentID " + getParentID()
        );
        buf.append(" entries:");
        for (int i = 0; i < numPairs; i++) {
            buf.append(dictionary[i].key + ",");
        }
        System.out.println(buf.toString());
    }

    public DictionaryPair getFirstDictionaryPair() {
        return dictionary[0];
    }

    public DictionaryPair getLastDictionaryPair() {
        return dictionary[numPairs - 1];
    }

    public void deleteFirstDictionaryPair() {
        delete(0);
    }

    public void deleteLastDictionaryPair() {
        delete(numPairs - 1);
    }

    /**
     * Given an index, this method sets the dictionary pair at that index within
     * the dictionary to null.
     *
     * @param index: the location within the dictionary to be set to null
     */
    public void delete(int index) {

        // Delete dictionary pair from leaf
        this.dictionary[index] = null;

        // Decrement numPairs
        numPairs--;

        shiftNullsToEnd();
    }

    /**
     * This method attempts to insert a dictionary pair within the dictionary of
     * the LeafNode object. If it succeeds, numPairs increments, the dictionary
     * is sorted, and the boolean true is returned. If the method fails, the
     * boolean false is returned.
     *
     * @param dp: the dictionary pair to be inserted
     * @return a boolean indicating whether or not the insert was successful
     */
    public boolean insert(DictionaryPair dp) {
        if (this.isFull()) {

            /* Flow of execution goes here when numPairs == maxNumPairs */
            return false;
        } else {

            // Insert dictionary pair in the correct position
            insertAndShift(dp);
            //insertAndSort(dp);
            return true;
        }
    }

    private void insertAndSort(DictionaryPair dp){
        this.dictionary[numPairs] = dp;
        numPairs++;
        Arrays.sort(this.dictionary, 0, numPairs);
    }
    
    private void insertAndShift(DictionaryPair dp) {
        int index = 0;
        // Find the correct position to insert the new element
        while (index < numPairs && dictionary[index].compareTo(dp) < 0) {
            index++;
        }

        // Shift elements to the right to make space for the new element
        for (int i = numPairs; i > index; i--) {
            this.dictionary[i] = dictionary[i - 1];
        }

        // Insert the new element at the correct position
        this.dictionary[index] = dp;

        // Increment the number of pairs
        numPairs++;
    }

    /**
     * This method inserts a dictionary pair into the beginning of this node.
     *
     * @param dp: the pair to be inserted
     */
    public void prependPair(DictionaryPair dp) {
        for (int i = numPairs - 1; i >= 0; i--) {
            this.dictionary[i + 1] = this.dictionary[i];
        }
        this.dictionary[0] = dp;
        this.numPairs++;

    }

    /**
     * This method inserts all pairs of a leaf node to the beginning of this
     * node.
     *
     * @param ln: the node from where the pairs are copied
     */
    public void prependPairs(LeafNode ln) {

        for (int i = numPairs - 1; i >= 0; i--) {
            this.dictionary[i + ln.numPairs] = this.dictionary[i];
        }

        for (int i = 0; i < ln.numPairs; i++) {
            this.dictionary[i] = ln.dictionary[i];
        }

        this.numPairs += ln.numPairs;

    }

    /**
     * This method appends all dictionary pairs of a leaf internal node into the
     * end of this leaf node
     *
     * @param ln: The leaf node from where the pairs are copied
     */
    public void appendPairs(LeafNode ln) {

        for (int i = 0; i < ln.numPairs; i++) {
            this.dictionary[numPairs + i] = ln.dictionary[i];
        }

        this.numPairs += ln.numPairs;

    }

    /**
     * This method appends a dictionary pair into the end of this leaf node
     *
     * @param dp: The pair to be copied
     */
    public void appendPair(DictionaryPair dp) {
        this.dictionary[numPairs] = dp;
        this.numPairs++;

    }

    /**
     * This simple method determines if the LeafNode is deficient, i.e. the
     * numPairs within the LeafNode object is below minNumPairs.
     *
     * @return a boolean indicating whether or not the LeafNode is deficient
     */
    public boolean isDeficient() {
        return numPairs < minNumPairs;
    }

    /**
     * This simple method determines if the LeafNode is full, i.e. the numPairs
     * within the LeafNode is equal to the maximum number of pairs.
     *
     * @return a boolean indicating whether or not the LeafNode is full
     */
    public boolean isFull() {
        return numPairs == maxNumPairs;
    }

    /**
     * This simple method determines if the LeafNode object is capable of
     * lending a dictionary pair to a deficient leaf node. The LeafNode object
     * can lend a dictionary pair if its numPairs is greater than the minimum
     * number of pairs it can hold.
     *
     * @return a boolean indicating whether or not the LeafNode object can give
     * a dictionary pair to a deficient leaf node
     */
    public boolean isLendable() {
        return numPairs > minNumPairs;
    }

    /**
     * This simple method determines if the LeafNode object is capable of being
     * merged with, which occurs when the number of pairs within the LeafNode
     * object is lower or equal to the minimum number of pairs it can hold.
     *
     * @return a boolean indicating whether or not the LeafNode object can be
     * merged with
     */
    public boolean isMergeable() {
        return numPairs <= minNumPairs;
    }

    /**
     * This method splits a single dictionary into two dictionaries where all
     * dictionaries are of equal length, but each of the resulting dictionaries
     * holds half of the original dictionary's non-null values. This method is
     * primarily used when splitting a node within the B+ tree. The dictionary
     * of the specified LeafNode is modified in place. The method returns the
     * remainder of the DictionaryPairs that are no longer within ln's
     * dictionary.
     *
     * @param ln: list of DictionaryPairs to be split
     * @param split: the index at which the split occurs
     * @return DictionaryPair[] of the two split dictionaries
     */
    public DictionaryPair[] splitDictionary(int split) {

        /* Initialize two dictionaries that each hold half of the original
		   dictionary values */
        DictionaryPair[] halfDict = new DictionaryPair[maxNumPairs + 1];

        // Copy half of the values into halfDict
        for (int i = split; i < dictionary.length; i++) {
            halfDict[i - split] = dictionary[i];
            this.dictionary[i] = null;
            numPairs--;
        }

        shiftNullsToEnd();

        return halfDict;
    }

    /*
    * shift all dictionary pairs to the end of the array
    * useful when a pair is deleted, to group all pairs at the first positions of the array
    * to do: receive the position where the pair was deleted, so we dont have to traverse the whole array
     */
    public void shiftNullsToEnd() {
        int nonNullIndex = 0;
        for (int currentIndex = 0; currentIndex < dictionary.length; currentIndex++) {
            if (dictionary[currentIndex] != null) {
                // Swap non-null element to the front of the array
                DictionaryPair temp = dictionary[currentIndex];
                dictionary[currentIndex] = dictionary[nonNullIndex];
                dictionary[nonNullIndex] = temp;
                nonNullIndex++;
            }
        }
    }

    /**
     * This is a specialized sorting method used upon lists of DictionaryPairs
     * that may contain interspersed null values.
     *
     * @param dictionary: a list of DictionaryPair objects
     */
    private void sortDictionary() {
        Arrays.sort(dictionary, new Comparator<DictionaryPair>() {
            @Override
            public int compare(DictionaryPair o1, DictionaryPair o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o1 == null) {
                    return 1;
                }
                if (o2 == null) {
                    return -1;
                }
                return o1.compareTo(o2);
            }
        });
    }

    /*
    * adds a dictionarypair and sorts the array so the pair is put at the proper position
    * to do: replace with a method that receives the proper position, so we can shift instead of sort
     */
    public void sortDictionary(Key key, Value value) {

//        dictionary[numPairs] = new DictionaryPair(key, value, tree);
//        numPairs++;
//        sortDictionary();
        
        DictionaryPair dp = new DictionaryPair(key, value, tree);
        //insertAndShift(dp);
        insertAndSort(dp);
    }

    @Override
    public int getHeaderSizeInBytes() {
        return super.getHeaderSizeInBytes() + 5 * Integer.BYTES;
    }

    @Override
    public void writeExternal(DataOutput out) throws IOException {
        out.writeInt(maxNumPairs);
        out.writeInt(numPairs);
        out.writeInt(getParentID());
        out.writeInt(leftSiblingID);
        out.writeInt(rightSiblingID);
        for (int i = 0; i < numPairs; i++) {
            dictionary[i].writeExternal(out);
            //out.writeObject(dictionary[i]);
        }
    }

    @Override
    public void readExternal(DataInput in) throws IOException {
        this.maxNumPairs = in.readInt();
        this.numPairs = in.readInt();

        //this.parentID = in.readInt();
        this.setParentID(in.readInt());
        this.leftSiblingID = in.readInt();
        this.rightSiblingID = in.readInt();

        setMinNumPairs();

        dictionary = new DictionaryPair[maxNumPairs + 1];
        for (int i = 0; i < numPairs; i++) {
            DictionaryPair dp = new DictionaryPair(tree);
            dp.readExternal(in);
            dictionary[i] = dp;
            //dictionary[i] = (DictionaryPair)in.readObject();
        }

    }

    /*
    * Returns the maximum number of entries of a leaf node considering the pageSize, the fixed header size and the number of bytes for each entry
    * An antry is formed by a key and a value
     */
    public int findOutOptimalDegree(long pageSize, long keySize, long valueSize) {
        return (int) Math.floor((pageSize - getHeaderSizeInBytes()) / (keySize + valueSize));
    }

    @Override
    public String toString() {
        String aux = "";
        for (int i = 0; i < numPairs; i++) {
            aux += dictionary[i].key + "--";
        }

        return aux;
    }

}
