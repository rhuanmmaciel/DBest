/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree;

import ibd.persistent.ExternalizablePage;
import java.util.Arrays;

/**
 *
 * @author Sergio
 */
public abstract class Key implements Comparable<Key>, ExternalizablePage {

    //A key is composed by a number of comparable objects
    protected Comparable[] keys;


    /*
    * sets all objects that are part of a key
    * this function does not verify if the objects conform with the expected datatype, as described in the schema
     */
    public void setKeys(Comparable[] keys) {
        this.keys = keys;
    }

    public Comparable get(int index) {
        return keys[index];
    }

    /*
    * returns a string representation of the key considering the concatenatin of its first parts
     */
    public String getPartialKey(int parts) {
        String result = "";
        for (int i = 0; i < parts; i++) {
            result += keys[i] + ",";
        }

        return result;

    }

    @Override
    public boolean equals(Object o) {
        Key k1 = (Key) o;
        //return Arrays.equals(keys, k1.keys);

        int res = this.compareTo(k1);
        return (res == 0);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Arrays.deepHashCode(this.keys);
        return hash;
    }

    
    /**
     * This is a method that allows comparisons to take place between
     * DictionaryPair objects in order to sort them later on
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(Key other) {
        Key key1 = (Key) other;
        
        int minLength = Math.min(keys.length, key1.keys.length);

        for (int i = 0; i < minLength; i++) {
            int res = (keys[i].compareTo(key1.keys[i]));
            if (res != 0) {
                return res;
            }
        }
        // If the elements are equal, continue to the next position

        // If all the elements up to minLength are equal, the longer array is considered higher
        if (keys.length > key1.keys.length) {
            return 1;
        } else if (keys.length < key1.keys.length) {
            return -1;
        } else {
            return 0;
        }
        
    }
    
    /**
     * returns true if the first set levels of this key are equal to the first levels of other key
     * @param otherKey the key to be compared with
     * @return
     */
    public boolean partialMatch(Key otherKey) {
        //if the number of existing levels in lower than the number of levels to be compared
        int minLength = Math.min(keys.length, otherKey.keys.length);

        for (int i = 0; i < minLength; i++) {
            int res = keys[i].compareTo(otherKey.keys[i]);
            if (res != 0) {
                return false;
            }
        }
        
        
        //all first levels have equal values
        return true;
    }

    
    @Override
    public String toString() {
        return getPartialKey(keys.length);
    }

    /*
    * the size refers to the amount of bytes taken by each object of the key.
     */
    @Override
    public int getSizeInBytes() {
        return 0;
    }

}
