/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree.generic;

import ibd.index.btree.Key;
import ibd.index.btree.Value;
import ibd.persistent.PersistentPageFile;
import java.nio.file.Paths;

/**
 *
 * @author Sergio
 */
public class Test {

    public boolean add(BPlusTreeFileGeneric tree, RowSchema keySchema, RowSchema valueSchema, long value1, long value2, long value3) {
        Key key = new PrimitiveKey(keySchema);
        key.setKeys(new Long[]{value1, value2, value3});
        Value value = tree.createValue();
        value.set(0, "record:" + (value1 + "-" + value2 + "-" + value3));

        return tree.insert(key, value, true);

    }

    public boolean delete(BPlusTreeFileGeneric tree, RowSchema keySchema, long value1, long value2, long value3) {
        Key key = new PrimitiveKey(keySchema);
        key.setKeys(new Long[]{value1, value2, value3});

        Value value = tree.delete(key);
        return (value != null);

    }

    public String query(BPlusTreeFileGeneric tree, RowSchema keySchema, long value1, long value2, long value3) {
        Key key = new PrimitiveKey(keySchema);
        key.setKeys(new Long[]{value1, value2, value3});

        Value value = tree.search(key);
        if (value != null) {
            return value.toString();
        }

        return null;
    }

    public void queryInserted(BPlusTreeFileGeneric tree, RowSchema keySchema, long value1, long value2, long value3) {
        Key key = new PrimitiveKey(keySchema);
        key.setKeys(new Long[]{value1, value2, value3});
        String result = query(tree, keySchema, value1, value2, value3);
        if (result != null) {
            System.out.println("Success. Found " + result);
        } else {
            System.out.println("Error. Not found " + value1 + "-" + value2 + "-" + value3);
        }
    }

    public void queryDeleted(BPlusTreeFileGeneric tree, RowSchema keySchema, long value1, long value2, long value3) {
        Key key = new PrimitiveKey(keySchema);
        key.setKeys(new Long[]{value1, value2, value3});
        String result = query(tree, keySchema, value1, value2, value3);
        if (result == null) {
            System.out.println("Success. Not found " + value1 + "-" + value2 + "-" + value3);
        } else {
            System.out.println("Error. Should not find " + value1 + "-" + value2 + "-" + value3);
        }
    }

    public static void main(String[] args) {
        try {

            boolean newDatabase = true;

            RowSchema keySchema = new RowSchema(3);
            keySchema.addLongDataType();
            keySchema.addLongDataType();
            keySchema.addLongDataType();

            RowSchema valueSchema = new RowSchema(1);
            valueSchema.addStringDataType();

            PersistentPageFile p = new PersistentPageFile(4096, Paths.get("c:\\teste\\mtree\\mtree"), newDatabase);
            BPlusTreeFileGeneric tree = new BPlusTreeFileGeneric(p, keySchema, valueSchema);
            tree.open();
            Test test = new Test();

            if (newDatabase) {
                test.add(tree, keySchema, valueSchema, 0, 0, 0);
                test.add(tree, keySchema, valueSchema, 0, 0, 1);
                test.add(tree, keySchema, valueSchema, 0, 0, 2);
                test.delete(tree, keySchema, 0, 0, 0);
            }

            test.queryInserted(tree, keySchema, 0, 0, 1);
            test.queryDeleted(tree, keySchema, 0, 0, 0);
            test.queryInserted(tree, keySchema, 0, 0, 2);
//            test.update(tree, 1,

            tree.close();
        } catch (Exception ex) {
            //Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

}
