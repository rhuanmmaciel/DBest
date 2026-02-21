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
public class AllKeysIterator implements Iterator<DictionaryPair>{

    //stores the next value to be returned by the next() function
    DictionaryPair nextValue = null;
    LeafNode curNode;
    DictionaryPair dps[];
    BPlusTreeFile btree;

    int index;
    
    public AllKeysIterator(BPlusTreeFile btree){
        this.btree = btree;
        curNode = (LeafNode) btree.getNode(btree.getFirstLeafID());
        dps = curNode.dictionary;
        index = 0;
    }

    /**
     * Finds the next value. If the next value was already computed by
     * hasNext(), use it.
     *
     * @return
     */
    @Override
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
    @Override
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
                nextValue = dps[index];
                index++;
                return nextValue;

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