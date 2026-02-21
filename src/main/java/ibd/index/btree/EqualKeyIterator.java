/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.index.btree;

import engine.info.Parameters;
import java.util.Iterator;

/**
 * This class defines the behavior of the iterator functions next() and
 * hasNext()
 *
 * @author Sergio
 */
public class EqualKeyIterator implements Iterator<DictionaryPair>{

    //stores the next value to be returned by the next() function
    DictionaryPair nextValue = null;
    LeafNode curNode;
    DictionaryPair dps[];
    BPlusTreeFile btree;
    Key key;
    int index;
    
    public EqualKeyIterator(BPlusTreeFile btree,Key key){
        this.btree = btree;
        this.key = key;
        //performs the search over the b-tree
        curNode = btree.getFirstPage(key);
        dps = curNode.dictionary;
        // Perform binary search to find index of key within dictionary
        index = Utils.binarySearch(dps, curNode.numPairs, key, btree);

        // in this case, a negative index may simply mean that the key has less levels than the indexed keys.
        // we need to access the largest value smaller than the key, and advance one position.
        if (index < 0) {
            index = ~index;
        } //the bynary search might not retrieve the first matching value
        //so we need to search back until the first matching position is found
        else {
            while (index > 0 && dps[index - 1].key.compareTo(key) == 0) {
                index--;
            }
        }
    }

    /**
     * Finds the next value. If the next value was already computed by
     * hasNext(), use it.
     *
     * @return
     */
    public DictionaryPair next() {

        if (nextValue != null) {
            DictionaryPair next_ = nextValue;
            nextValue = null;
            return next_;
        }

        return findNextTuple();

    }

    /**
     * verifies if there is a next value. Is there is one, store it so the call
     * to next may access it.
     *
     * @return true if there is a next value
     */
    public boolean hasNext() {

        if (nextValue != null) {
            return true;
        }
        nextValue = findNextTuple();

        return (nextValue != null);
    }

    /**
     * This is the function that actually locates the next value.
     *
     * @return the next value, or null if there isnt any.
     */
    protected DictionaryPair findNextTuple() {

        while (curNode != null) {
            // Iterate through the dictionary of each node
            while (index < dps.length) {
                if (dps[index] == null) {
                    break;
                }
                Parameters.RECORDS_READ++;
                if (key.partialMatch(dps[index].key)) {
                    nextValue = dps[index];
                    index++;
                    return nextValue;
                } 
                return null;

            }


            /* Update the current node to be the right sibling,
			   leaf traversal is from left to right */
            curNode = (LeafNode) btree.getNode(curNode.rightSiblingID);
            if (curNode!=null){
                dps = curNode.dictionary;
                index =0;
            }
        }
        return null;

    }
}