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
public abstract class Value implements ExternalizablePage {

    //A value is composed by a number of objects
    protected Object[] objects;


    public Object get(int index) {
        return objects[index];
    }

    /*
    * sets an object to a specific posistion of the obejcts array
    * this function does not verify if the object conform with the expected datatype, as described in the schema
     */
    public void set(int index, Object value) {
        objects[index] = value;
    }

    public int size() {
        return objects.length;
    }

//    public Integer getInt(int index) {
//        return (Integer) array[index];
//    }
//
//    public void setInt(int index, int value) {
//        array[index] = value;
//    }
//
//    public String getString(int index) {
//        return (String) array[index];
//    }
//
//    public void setString(int index, String value) {
//        array[index] = value;
//    }
    // Add more specific getter and setter methods for other types as needed
//    public String getObject(){
//        return object;
//    }
    @Override
    public String toString() {
        return Arrays.toString(objects);
    }

    /*
    * the size refers to the amount of bytes taken by each object of the value.
     */
    public abstract int getSizeInBytes();

}
