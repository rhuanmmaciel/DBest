/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree;

import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author Sergio
 */
public class Utils {
    
    
    /**
     * This method performs a standard binary search on a sorted
     * DictionaryPair[] and returns the index of the dictionary pair with target
     * key t if found. Otherwise, this method returns a negative value.
     *
     * @param dps: list of dictionary pairs sorted by key within leaf node
     * @param t: target key value of dictionary pair being searched for
     * @return index of the target value if found, else a negative value
     */
    public static int binarySearch(DictionaryPair[] dps, int numPairs, Key t, BPlusTree tree) {
        Comparator<DictionaryPair> c = new Comparator<DictionaryPair>() {
            @Override
            public int compare(DictionaryPair o1, DictionaryPair o2) {
                return o1.compareTo(o2);
            }
        };
        return Arrays.binarySearch(dps, 0, numPairs, new DictionaryPair(t, null, tree), c);
    }
    
    /**
     * This method performs a standard linear search on a sorted
     * DictionaryPair[] and returns the index of the first null entry found.
     * Otherwise, this method returns a -1. This method is primarily used in
     * place of binarySearch() when the target t = null.
     *
     * @param dps: list of dictionary pairs sorted by key within leaf node
     * @return index of the target value if found, else -1
     */
    public static int linearNullSearch(DictionaryPair[] dps) {
        for (int i = 0; i < dps.length; i++) {
            if (dps[i] == null) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * This method performs a standard linear search on a list of Node[]
     * pointers and returns the index of the first null entry found. Otherwise,
     * this method returns a -1. This method is primarily used in place of
     * binarySearch() when the target t = null.
     *
     * @param pointers: list of Node[] pointers
     * @return index of the target value if found, else -1
     */
    public static int linearNullSearch(Node[] pointers) {
        for (int i = 0; i < pointers.length; i++) {
            if (pointers[i] == null) {
                return i;
            }
        }
        return -1;
    }
    
    public static int linearNullSearch(Integer[] pointers) {
        for (int i = 0; i < pointers.length; i++) {
            if (pointers[i] == null) {
                return i;
            }
        }
        return -1;
    }
}


