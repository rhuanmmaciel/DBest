/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree;

import java.util.ArrayList;

/**
 * Defines the structure of a B+ tree.
 * The tree enables the basic operations defined in terms of keys and values.
 * @author 
 */
public abstract class BPlusTree {

    //the order (fanout) of the B+ tree
    protected int m;
    //the number of values stores at the leaf nodes
    protected int leafM;
    
    /**
     * Creates a key.
     * The instantiated class determines the contents stored by the key 
     * @return the instantiated key
     */
    public abstract Key createKey();
    
    /**
     * Creates a value.
     * The instantiated class determines the contents stored by the value 
     * @return the instantiated value
     */
    public abstract Value createValue();
    
    /**
     * Given a key, this method will remove the dictionary pair with the
     * corresponding key from the B+ tree.
     *
     * @param key: a value that corresponds with an existing dictionary pair
     * @return the value associated with the key or null if it does not exist
     */
    public abstract Value delete(Key key);
    
    /**
     * Given a key and a value, this method inserts a
     * dictionary pair accordingly into the B+ tree.
     *
     * @param key: a key to be used in the dictionary pair
     * @param value: a value to be used in the dictionary pair
     * @param unique: no duplicate values are allowed for the key part
     * @return 
     */
    public abstract boolean insert(Key key, Value value, boolean unique);
    
    /**
     * Given a key, this method returns the value associated with the key within
     * a dictionary pair that exists inside the B+ tree.
     *
     * @param key: the key to be searched within the B+ tree
     * @return the value associated with the key within the B+
     * tree
     */
    public abstract Value search(Key key);
    

    /**
     * This method traverses the doubly linked list of the B+ tree leaf nodes and finds
     * all values whose associated keys are within the range specified by
     * lowerBound and upperBound.
     *
     * @param lowerBound: (int) the lower bound of the range
     * @param upperBound: (int) the upper bound of the range
     * @return an ArrayList<Value> that holds all values of dictionary pairs
     * whose keys satisfy are search conditions.
     */
    public abstract ArrayList<Value> search(Key lowerBound, Key upperBound);

    /**
     * This method traverses the doubly linked list of the B+ tree leaf nodes and finds
     * all values.
     * @return an ArrayList<DictionaryPair> that holds all dictionary pairs
     * 
     */
    public abstract ArrayList<DictionaryPair> searchAll();
    
    /**
     * This method traverses the doubly linked list of the B+ tree leaf nodes and finds
     * all values whose associated keys have a partial match with a given key.
     *
     * @param key: (Key) the key to be compared with
     * @return an ArrayList<Value> that holds all values of dictionary pairs
     * whose key satisfy the search condition.
     */
    public abstract ArrayList<Value> partialSearch(Key key);
    
    /**
     * This method verifies if the tree indexes at least one value that have a partial match with a given key.
     *
     * @param key: the key to be compared with
     * @return true if at least one values satisfies the search condition.
     */
    public abstract boolean contains(Key key);

    /**
     * This method updates the value of the DictionaryPair that holds a given key.
     *
     * @param key: the key to be searched within the B+ tree
     * @param value: the value to replace the current indexed value
     * @return the value updated, or null if the DictionaryPair was not found
     */
    public abstract Value update(Key key, Value value);
    
    /**
     * This method traverses the doubly linked list of the B+ tree leaf nodes and finds
     * all values.
     * @return an Iterator to access all dictionary pairs
     * 
     */
    public abstract AllKeysIterator searchAllIterator();
    

    
}
