/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree.table;

import ibd.index.btree.BPlusTree;
import ibd.index.btree.Key;
import ibd.index.btree.Value;
import ibd.persistent.PersistentPageFile;
import ibd.table.prototype.LinkedDataRow;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.StringColumn;

/**
 *
 * @author Sergio
 */
public class Test_1 {
    
    Prototype prototype;
    
    Hashtable<Key, Value> dic = new Hashtable();
    
    public ArrayList<String> createOperations() {
        ArrayList operations = new ArrayList();
        operations.add("delete,6");
        operations.add("insert,40");
        operations.add("insert,48");
        operations.add("insert,35");
        operations.add("delete,4");
        operations.add("delete,19");
        operations.add("delete,17");
        operations.add("insert,38");
        operations.add("delete,31");
        operations.add("insert,41");
        operations.add("delete,38");
        operations.add("delete,22");
        operations.add("delete,16");
        operations.add("delete,14");
        operations.add("delete,26");
        operations.add("insert,44");
        operations.add("delete,41");
        operations.add("delete,27");
        
        return operations;
    }
    
    public ArrayList<Key> createList(BPlusTreeFileTable tree,  int size) {
        Random r = new Random();
        
        Hashtable<String, String> dic_ = new Hashtable<String, String>();
        ArrayList<Key> list = new ArrayList();
        
        int count = 0;
        for (int i = 0; i < size; i++) {
            int level = r.nextInt(2000);
            int outerSeq = r.nextInt(2000);
            int innerSeq = r.nextInt(2000);
            String k = level + "," + outerSeq + "," + innerSeq;
            if (dic_.containsKey(k)) {
                continue;
            }
            
            dic_.put(k, k);

            //System.out.println("insert "+count);
            count++;
            
            LinkedDataRow keyData = new LinkedDataRow(this.prototype, false);
            keyData.setInt("id1", level);
            keyData.setInt("id2", outerSeq);
            keyData.setInt("id3", innerSeq);
            
            Key key = new BinaryKey(prototype);
            key.setKeys(new DataRow[]{keyData});
            
            LinkedDataRow valueData = new LinkedDataRow(this.prototype, false);
            valueData.setInt("id1", level);
            valueData.setInt("id2", outerSeq);
            valueData.setInt("id3", innerSeq);
            valueData.setString("nome", level + "," + outerSeq + "," + innerSeq);
            
            Value value = tree.createValue();
            value.set(0, valueData);
            
            list.add(key);
            dic.put(key, value);
            
        }
        return list;
        
    }
    
    public ArrayList<Key> createList(BPlusTreeFileTable tree, int start, int end, boolean shuffle, boolean reverse) {
        ArrayList<Key> list = new ArrayList();
        Random r = new Random();
        for (int i = start; i < end; i++) {
            LinkedDataRow keyData = new LinkedDataRow(this.prototype, false);
            keyData.setInt("id1", 5);
            keyData.setInt("id2", 5);
            keyData.setInt("id3", i);
            
            Key key = new BinaryKey(prototype);
            key.setKeys(new DataRow[]{keyData});
            
            LinkedDataRow valueData = new LinkedDataRow(this.prototype, false);
            valueData.setInt("id1", 5);
            valueData.setInt("id2", 5);
            valueData.setInt("id3", i);
            valueData.setString("nome", "5,5," + i);
            
            Value value = tree.createValue();
            byte[] bytes = prototype.convertToArray(valueData);
            value.set(0, bytes);
            
            list.add(key);
            dic.put(key, value);
        }
        
        if (reverse) {
            Collections.reverse(list);
        } else if (shuffle) {
            Collections.shuffle(list);
        }
        return list;
        
    }
    
    public void createList2(BPlusTreeFileTable tree, int start1, int end1, int end2, int innerSeq) {
        Random r = new Random();
        ArrayList<Key> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int level = start1 + r.nextInt(end1 - start1);
            int outerSeq = r.nextInt(end2);
            
            LinkedDataRow keyData = new LinkedDataRow(this.prototype, false);
            keyData.setInt("id1", level);
            keyData.setInt("id2", outerSeq);
            keyData.setInt("id3", innerSeq);
            
            Key key = new BinaryKey(prototype);
            key.setKeys(new DataRow[]{keyData});
            
            LinkedDataRow valueData = new LinkedDataRow(this.prototype, false);
            valueData.setInt("id1", level);
            valueData.setInt("id2", outerSeq);
            valueData.setInt("id3", innerSeq);
            valueData.setString("nome", level + "," + outerSeq + "," + innerSeq);
            
            Value value = tree.createValue();
            value.set(0, valueData);
            dic.put(key, value);
            
            list.add(key);
        }
    }
    
    public ArrayList<Key> createList3(BPlusTreeFileTable tree, int start1, int end1, int end2) {
        
        ArrayList<Key> list = new ArrayList();
        
        for (int i = start1; i < end1; i++) {
            for (int j = 0; j < end2; j++) {
                for (int l = 0; l < 10; l++) {
                    
                    LinkedDataRow keyData = new LinkedDataRow(this.prototype, false);
                    keyData.setInt("id1", i);
                    keyData.setInt("id2", j);
                    keyData.setInt("id3", l);
                    
                    Key key = new BinaryKey(prototype);
                    key.setKeys(new DataRow[]{keyData});
                    
                    LinkedDataRow valueData = new LinkedDataRow(this.prototype, false);
                    valueData.setInt("id1", i);
                    valueData.setInt("id2", j);
                    valueData.setInt("id3", l);
                    valueData.setString("nome", i + "," + j + "," + l);
                    
                    Value value = tree.createValue();
                    value.set(0, valueData);
                    dic.put(key, value);
                    
                    list.add(key);
                }
            }
        }
        return list;
        
    }
    
    private void insert(BPlusTree tree, Key key) {
        
        Value value = dic.get(key);
        tree.insert(key, value, true);
        
    }
    
    public void insert4(BPlusTreeFileTable tree, ArrayList<Key> list) {
        
        for (Key key : list) {
            //System.out.println("insert "+key);
            Value value = dic.get(key);
            tree.insert(key, value, true);
        }
        
    }
    
    public void delete(BPlusTreeFileTable tree, Key query) {
        
        System.out.println("removing key " + query);
        tree.delete(query);
        
    }
    
    public void delete4(BPlusTreeFileTable tree, ArrayList<Key> list) {

        //for (Key key : list) {
        for (int i = 0; i < list.size(); i++) {
            Key key = list.get(i);
            
            System.out.println("delete " + key);
            
            tree.delete(key);
            for (int j = i + 1; j < list.size(); j++) {
//                Key key2 = list.get(j);
//                Value v = tree.search(key2);
//                if (v == null) {
//                    System.out.println("err00000000000000000000000000000000ooooo ");
//                }
            }
//            Value v = tree.search(new Key(0,0,1));
//            if (v==null){
//                System.out.println("perdeeeeeeeeeeeeeeeeuuuuuuuuuu");
//                tree.printTree();
//                
//                return;
//            }
//            
//            if (key.innerSeq==2){
//                tree.printTree();
//                
//                tree.check();
//                return;
//            }

        }
        
    }
    
    public void query(BPlusTreeFileTable tree, Key query) {

        //System.out.println("looking for query " + query);
        Value value = tree.search(query);
        if (value != null) {
            //System.out.println("achei " + value);
        } else {
            System.out.println("não achei " + query);
        }
        
    }
    
    public void query4(BPlusTreeFileTable tree, ArrayList<Key> list, ArrayList<String> operations) throws Exception {

        //System.out.println("searching all");
        for (int i = 0; i < list.size(); i++) {
            
            Key key = list.get(i);
            Value value = tree.search(key);
            if (value == null) {
                System.out.println("not found " + key);
                System.out.println("last operation = " + lastOp);
                
                for (String operation : operations) {
                    System.out.println(operation);
                }
                System.exit(1);
                //throw new Exception("not found " + key);
            }            
        }
        System.out.println("ineserted test OK");
    }
    
    public void deletedQuery4(BPlusTreeFileTable tree, ArrayList<Key> list, ArrayList<String> operations) throws Exception {

        //System.out.println("searching all");
        for (int i = 0; i < list.size(); i++) {
            
            Key key = list.get(i);
            //if (i==list.size()-1)
            //System.out.println("aqio");
            Value value = tree.search(key);
            if (value != null) {
                System.out.println("found " + key);
                System.out.println("last operation = " + lastOp);
                for (String operation : operations) {
                    System.out.println(operation);
                }
                System.exit(1);
                throw new Exception("not found " + key);
            }
        }
        System.out.println("deleted test OK");
    }
    
    public void partialQuery(BPlusTreeFileTable tree, ArrayList<Key> list) {
        Random r = new Random();
        for (int i = 0; i < list.size(); i++) {
            //level = 1;
            Key query = list.get(i);
            System.out.println("looking for query " + query);
            ArrayList<Value> values = tree.partialSearch(query);
            for (Value value : values) {
                System.out.println(value);
            }
            
        }
    }
    
    public void update(BPlusTreeFileTable tree, Key query, int newChild) {
        
        Value value = tree.search(query);
        
        if (value != null) {
            //value.outerSeqChild = newChild;
            //tree.update(query, value);
            System.out.println("atualizei " + value);
        } else {
            System.out.println("não atualizei");
        }
        
    }
    
    String lastOp = "";
    
    public void testHard(int iterations, BPlusTreeFileTable tree) throws Exception {
        Random random = new Random();
        int max = 50;
        
        ArrayList<Key> keys = createList(tree, 0, 32, false, false);
        insert4(tree, keys);
        ArrayList<Key> partialKeys = new ArrayList();
        for (Key key : keys) {
            Long part1 = (Long) key.get(0);
            if (part1 == 0) {
                partialKeys.add(key);
            }
        }
        
        ArrayList<String> operations = new ArrayList();
        
        ArrayList<Key> deletedKeys = new ArrayList();

//        for (int i = 32; i < max; i++) {
//            Key key = new Key(tree.getKeySchema());
//            key.setKeys(new Long[]{new Long(0), new Long(0), new Long(i)});
//            deletedKeys.add(key);
//        }
        //while (true) {
        for (int i = 0; i < iterations; i++) {
            System.out.println(i);
            int index = random.nextInt(max);
            LinkedDataRow keyData = new LinkedDataRow(this.prototype, false);
            keyData.setInt("id1", random.nextInt(5));
            keyData.setInt("id2", random.nextInt(5));
            keyData.setInt("id3", index);
            
            Key key = new BinaryKey(prototype);
            key.setKeys(new DataRow[]{keyData});
            
            int option = random.nextInt(2);
            if (option == 0) {
                LinkedDataRow rowData = new LinkedDataRow(this.prototype, false);
                rowData.setInt("id1", random.nextInt(5));
                rowData.setInt("id2", random.nextInt(5));
                rowData.setInt("id3", index);
                rowData.setString("nome", key.toString());
                Value value = tree.createValue();
                //value.setObject(10);

                byte[] bytes = prototype.convertToArray(rowData);
                value.set(0, bytes);
                
                if (!keys.contains(key)) {
                    Long part1 = (Long) key.get(0);
                    if (part1 == 0) {
                        partialKeys.add(key);
                    }
                    //System.out.println("insert "+key.toString());
                    operations.add("operations.add(\"insert," + key.get(2) + "\");");
                    lastOp = "operations.add(\"insert," + key.get(2) + "\");";
                    tree.insert(key, value, true);
                    keys.add(key);
                    deletedKeys.remove(key);
                }
            } else //System.out.println("remove "+key.toString());
            if (!deletedKeys.contains(key)) {
                //System.out.println("delete "+key.toString());
                operations.add("operations.add(\"delete," + key.get(2) + "\");");
                lastOp = "operations.add(\"delete," + key.get(2) + "\");";
                tree.delete(key);
                keys.remove(key);
                deletedKeys.add(key);
            }

            //tree.printTree();
            query4(tree, keys, operations);
            deletedQuery4(tree, deletedKeys, operations);
            
        }
        
        System.out.println("******* PARTIAl KEYS *****");
        Collections.sort(partialKeys);
        for (Key partialKey : partialKeys) {
            System.out.println(partialKey);
        }
        System.out.println("******* PARTIAl KEYS *****");
        
        for (Key deletedKey : deletedKeys) {
            System.out.println("key = new Key(tree.getKeySchema());");            System.out.println("key.setKeys(new Long[]{Long.valueOf(" + deletedKey.get(0) + "L),"
                    + "  Long.valueOf(" + deletedKey.get(1) + "L),"
                    + "  Long.valueOf(" + deletedKey.get(2) + "L)});");
            System.out.println("deletedKeys.add(key);");
        }
        
        for (Key addedKey : keys) {
            System.out.println("key = new Key(tree.getKeySchema());");            System.out.println("key.setKeys(new Long[]{Long.valueOf(" + addedKey.get(0) + "L),"
                    + "  Long.valueOf(" + addedKey.get(1) + "L),"
                    + "  Long.valueOf(" + addedKey.get(2) + "L)});");
            System.out.println("keys.add(key);");
        }
        
    }
    
    public void testHard(BPlusTreeFileTable tree) throws Exception {
        Random random = new Random();
        int max = 50;
        
        ArrayList<Key> keys = new ArrayList<>();
        ArrayList<String> operations = new ArrayList();
        ArrayList<Key> deletedKeys = new ArrayList();
        Key key = null;
        
        query4(tree, keys, operations);
        deletedQuery4(tree, deletedKeys, operations);
        
    }
    
    public void testHard2(BPlusTreeFileTable tree) throws Exception {
        Random random = new Random();
        int max = 3200;
        
        ArrayList<Key> keys = createList(tree, 0, 3200, false, false);
        insert4(tree, keys);
        
        ArrayList<String> operations = new ArrayList();
        
        ArrayList<Key> deletedKeys = new ArrayList();
        for (int i = 3200; i < max; i++) {
            
            LinkedDataRow keyData = new LinkedDataRow(this.prototype, false);
            keyData.setInt("id1", 0);
            keyData.setInt("id2", 0);
            keyData.setInt("id3", i);
            Key key = new BinaryKey(prototype);
            key.setKeys(new DataRow[]{keyData});
            deletedKeys.add(key);
        }
        
        while (true) {
            
            int option = random.nextInt(2);
            if (option == 0) {
                for (int i = 0; i < 10; i++) {
                    int index = random.nextInt(max);
                    
                    LinkedDataRow keyData = new LinkedDataRow(this.prototype, false);
                    keyData.setInt("id1", 0);
                    keyData.setInt("id2", 0);
                    keyData.setInt("id3", index);
                    
                    Key key = new BinaryKey(prototype);
                    key.setKeys(new DataRow[]{keyData});
                    
                    LinkedDataRow rowData = new LinkedDataRow(this.prototype, false);
                    rowData.setInt("id1", 0);
                    rowData.setInt("id2", 0);
                    rowData.setInt("id3", index);
                    rowData.setString("nome", "xx");
                    
                    Value value = tree.createValue();
                    byte[] bytes = prototype.convertToArray(rowData);
                    value.set(0, bytes);
                    dic.put(key, value);

                    //System.out.println("insert "+key.toString());
                    if (!keys.contains(key)) {
                        //operations.add("operations.add(\"insert," + key.get(2) + "\");");
                        tree.insert(key, value, true);
                        keys.add(key);
                        deletedKeys.remove(key);
                    }
                    
                }
                
            } else {
                for (int i = 0; i < 10; i++) {
                    int index = random.nextInt(max);
                    
                    LinkedDataRow keyData = new LinkedDataRow(this.prototype, false);
                    keyData.setInt("id1", 0);
                    keyData.setInt("id2", 0);
                    keyData.setInt("id3", index);
                    
                    Key key = new BinaryKey(prototype);
                    key.setKeys(new DataRow[]{keyData});

                    //System.out.println("insert "+key.toString());
                    if (!deletedKeys.contains(key)) {
                        //operations.add("operations.add(\"delete," + key.get(2) + "\");");
                        tree.delete(key);
                        keys.remove(key);
                        deletedKeys.add(key);
                    }
                }
            }
            
            query4(tree, keys, operations);
            deletedQuery4(tree, deletedKeys, operations);
        }
        
    }
    
    public void testHard1(BPlusTreeFileTable tree, ArrayList<String> operations) throws Exception {
        Random random = new Random();
        int max = 50;
        
        ArrayList<Key> keys = createList(tree, 0, 32, false, false);
        insert4(tree, keys);
        
        ArrayList<Key> deletedKeys = new ArrayList();
        for (int i = 32; i < max; i++) {
            LinkedDataRow keyData = new LinkedDataRow(this.prototype, false);
            keyData.setInt("id1", 0);
            keyData.setInt("id2", 0);
            keyData.setInt("id3", i);
            
            Key key = new BinaryKey(prototype);
            key.setKeys(new DataRow[]{keyData});
            
            deletedKeys.add(key);
        }
        
        for (int i = 0; i < operations.size(); i++) {
            String s = operations.get(i);
            
            if (i == operations.size() - 1) {
                System.out.println("aqui");
            }
            String parts[] = s.split(",");
            LinkedDataRow keyData = new LinkedDataRow(this.prototype, false);
            keyData.setInt("id1", 0);
            keyData.setInt("id2", 0);
            keyData.setInt("id3", Integer.valueOf(parts[1]));
            if (parts[0].equals("insert")) {
                
                LinkedDataRow rowData = new LinkedDataRow(this.prototype, false);
                rowData.setInt("id1", 0);
                rowData.setInt("id2", 0);
                rowData.setInt("id3", Integer.valueOf(parts[1]));
                rowData.setString("nome", "yy");
                System.out.println(i + " insert " + rowData.toString());
                
                byte[] bytes = prototype.convertToArray(rowData);
                
                Key key = new BinaryKey(prototype);
                key.setKeys(new DataRow[]{keyData});
                
                Value value = tree.createValue();
                value.set(0, bytes);
                
                tree.insert(key, value, true);
                keys.add(key);
                deletedKeys.remove(key);
                
            } else {
                System.out.println(i + " remove " + keyData.toString());
                
                byte bytes[] = prototype.convertToArray(keyData);
                
                Key key = new BinaryKey(prototype);
                key.setKeys(new DataRow[]{keyData});
                
                tree.delete(key);
                keys.remove(key);
                deletedKeys.add(key);
            }
            
            query4(tree, keys, operations);
            deletedQuery4(tree, deletedKeys, operations);
        }
        
        query4(tree, keys, operations);
        
    }
    
    public static void main(String[] args) {
        try {
            Test_1 test = new Test_1();
            
            Prototype pt = new Prototype();
            pt.addColumn(new IntegerColumn("id1", true));
            pt.addColumn(new IntegerColumn("id2", true));
            pt.addColumn(new IntegerColumn("id3", true));
            pt.addColumn(new StringColumn("nome"));
            
            pt.validateColumns();
            test.prototype = pt;
            
            PersistentPageFile p = new PersistentPageFile(4096, Paths.get("c:\\teste\\mtree\\mtree"), true);
            BPlusTreeFileTable tree = new BPlusTreeFileTable(p, pt);
            tree.open();
            //bplustree1 tree = new bplustree1(204, 254);

            int start1 = 0;
            int end1 = 10;
            int end2 = 5;
            
            ArrayList<String> operations = new ArrayList<>();

//            list = test.insert3(tree, start1, end1, end2, true);
//            list = test.delete3(tree, start1, end1, end2, true);
//            list = test.insert3(tree, start1, end1, end2, true);
//            test.delete(tree, 0, 0, 0);
//            list = test.query3(tree, start1, end1, end2);
//
//            //test.delete(tree, 0, 0,1);
//            test.query(tree, 1, 1, 1);
//            test.update(tree, 1, 1, 1, 100);
//            test.query(tree, 1, 1, 1);
//            //test.query(tree, start1, end1, end2, 1);
//            //test.partialQuery(tree, start1, end1, end2);
//            System.out.println("insert");
//            ArrayList<Key> list1 = test.createList(0, 400, false, false);
//            test.insert4(tree, list1);
////            System.out.println("delete");
//            ArrayList<Key> list2 = test.createList(0, 400, false, true);
//            test.delete4(tree, list2);
//            list1.removeAll(list2);
//            //test.insert4(tree, list1);
//            test.query4(tree, list1, operations);
            //tree.printTree();
            //test.delete(tree, new Key(0,0,35));
            //test.delete(tree, new Key(0,0,34));
            //test.delete(tree, new Key(0,0,33));
            //test.delete(tree, new Key(0,0,32));
            //test.delete(tree, new Key(0,0,31));
            //tree.printTree();
            //test.delete(tree, new Key(0,0,30));
            //tree.printTree();
            //test.delete(tree, new Key(0,0,29));
            //tree.printTree();
            //tree.printTree();
            //test.insert4(tree, list2);
            //ArrayList<String> operations = test.createOperations();
            //operations.add("insert,40");
            //test.testHard(200, tree);
//            ArrayList<Value> values = tree.partialSearch(key);
//            for (Value value : values) {
//                System.out.println(value.toString());
//            }
            //test.testHard(tree);
            test.testHard2(tree);
            
            tree.close();
        } catch (Exception ex) {
            //Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }
    
}
