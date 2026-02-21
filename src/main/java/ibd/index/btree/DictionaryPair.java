/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree;

import ibd.persistent.ExternalizablePage;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 *
 * @author Sergio
 */
/**
 * This class represents a dictionary pair that is to be contained within the
 * leaf nodes of the B+ tree. The class implements the Comparable interface so
 * that the DictionaryPair objects can be sorted later on. 
 * It also implements ExternalizablePage so it knows how to serialize itself.
 */
public class DictionaryPair implements Comparable<DictionaryPair>, ExternalizablePage {

    Key key;
    Value value;
    BPlusTree tree;

    public DictionaryPair(BPlusTree tree) {
        this.tree = tree;
    }
    
    public Key getKey(){
        return key;
    }
    
    public Value getValue(){
        return value;
    }
    
    
    /**
     * Constructor
     *
     * @param key: the key of the key-value pair
     * @param value: the value of the key-value pair
     */
    public DictionaryPair(Key key, Value value, BPlusTree tree) {
        this.key = key;
        this.value = value;
        this.tree = tree;
    }

    
    /**
     * This is a method that allows comparisons to take place between
     * DictionaryPair objects in order to sort them later on
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(DictionaryPair o) {
        return key.compareTo(o.key);
    }

    @Override
    public void writeExternal(DataOutput out) throws IOException {
        key.writeExternal(out);
        value.writeExternal(out);
    }

    @Override
    public void readExternal(DataInput in) throws IOException {
        
        key = tree.createKey();
        key.readExternal(in);
        value = tree.createValue();
        
        value.readExternal(in);
        
    }
    
    @Override
    public int getSizeInBytes(){
        return key.getSizeInBytes()+value.getSizeInBytes();
    }
    
    @Override
    public String toString(){
        return key.toString()+","+value.toString();
    }
    
}
