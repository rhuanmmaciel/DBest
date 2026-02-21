/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.index.btree.generic;

import ibd.index.btree.BPlusTree;
import ibd.index.btree.Key;
import ibd.index.btree.Value;
import ibd.persistent.PersistentPageFile;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;

/**
 *
 * @author Sergio
 */
public class Test_11 {

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

    public ArrayList<Key> createList(RowSchema prototype, int size) {
        Random r = new Random();

        Hashtable<String, String> dic = new Hashtable<String, String>();
        ArrayList<Key> list = new ArrayList();

        int count = 0;
        for (int i = 0; i < size; i++) {
            int level = r.nextInt(2000);
            int outerSeq = r.nextInt(2000);
            int innerSeq = r.nextInt(2000);
            String k = level + "," + outerSeq + "," + innerSeq;
            if (dic.containsKey(k)) {
                continue;
            }

            dic.put(k, k);

            //System.out.println("insert "+count);
            count++;
            Key key = new PrimitiveKey(prototype);
            key.setKeys(new Long[]{Long.valueOf(level), Long.valueOf(outerSeq), Long.valueOf(innerSeq)});
            list.add(key);

        }
        return list;

    }

    public ArrayList<Key> createList(RowSchema prototype, int start, int end, boolean shuffle, boolean reverse) {
        ArrayList<Key> list = new ArrayList();
        Random r = new Random();
        for (int i = start; i < end; i++) {
            Key key = new PrimitiveKey(prototype);
            key.setKeys(new Long[]{Long.valueOf(r.nextInt(5)), Long.valueOf(r.nextInt(5)), Long.valueOf(i)});
            list.add(key);
        }

        if (reverse) {
            Collections.reverse(list);
        } else if (shuffle) {
            Collections.shuffle(list);
        }
        return list;

    }

    public void createList2(RowSchema prototype, int start1, int end1, int end2, int innerSeq) {
        Random r = new Random();
        ArrayList<Key> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int level = start1 + r.nextInt(end1 - start1);
            int outerSeq = r.nextInt(end2);

            Key query = new PrimitiveKey(prototype);
            query.setKeys(new Long[]{Long.valueOf(level), Long.valueOf(outerSeq), Long.valueOf(innerSeq)});
            list.add(query);
        }
    }

    public ArrayList<Key> createList3(RowSchema prototype, int start1, int end1, int end2) {

        ArrayList<Key> list = new ArrayList();

        for (int i = start1; i < end1; i++) {
            for (int j = 0; j < end2; j++) {
                for (int l = 0; l < 10; l++) {
                    Key key = new PrimitiveKey(prototype);
                    key.setKeys(new Long[]{Long.valueOf(i), Long.valueOf(j), Long.valueOf(l)});
                    list.add(key);
                }
            }
        }
        return list;

    }

    private void insert(BPlusTree tree, Key key) {
        Value value = tree.createValue();
        value.set(0, "zz");
        tree.insert(key, value, true);

    }

    public void insert4(BPlusTreeFileGeneric tree, ArrayList<Key> list) {

        for (Key key : list) {
            //System.out.println("insert "+key);
            Value value = tree.createValue();
            //value.setObject(10);
            value.set(0, key.toString());
            tree.insert(key, value, true);
        }

    }

    
    
    public void delete(BPlusTreeFileGeneric tree, Key query) {

        System.out.println("removing key " + query);
        tree.delete(query);

    }

    public void delete4(BPlusTreeFileGeneric tree, ArrayList<Key> list) {

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
//            Value v = tree.search(new KeyPrimitive(0,0,1));
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

    public void query(BPlusTreeFileGeneric tree, Key query) {

        //System.out.println("looking for query " + query);
        Value value = tree.search(query);
        if (value != null) {
            //System.out.println("achei " + value);
        } else {
            System.out.println("não achei " + query);
        }

    }

    public void query4(BPlusTreeFileGeneric tree, ArrayList<Key> list, ArrayList<String> operations) throws Exception {

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

    public void deletedQuery4(BPlusTreeFileGeneric tree, ArrayList<Key> list, ArrayList<String> operations) throws Exception {

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

    public void partialQuery(BPlusTreeFileGeneric tree, ArrayList<Key> list) {
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

    public void update(BPlusTreeFileGeneric tree, Key query, int newChild) {

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

    public void testHard(int iterations, BPlusTreeFileGeneric tree) throws Exception {
        Random random = new Random();
        int max = 50;

        ArrayList<Key> keys = createList(tree.getKeySchema(), 0, 32, false, false);
        insert4(tree, keys);
        ArrayList<Key> partialKeys = new ArrayList();
        for (Key key : keys) {
            Long part1 = (Long)key.get(0);
                    if (part1==0)
                        partialKeys.add(key);
        }

        ArrayList<String> operations = new ArrayList();

        ArrayList<Key> deletedKeys = new ArrayList();
        
        
//        for (int i = 32; i < max; i++) {
//            Key key = new KeyPrimitive(tree.getKeySchema());
//            key.setKeys(new Long[]{new Long(0), new Long(0), new Long(i)});
//            deletedKeys.add(key);
//        }

        //while (true) {
        for (int i = 0; i < iterations; i++) {
            System.out.println(i);
            int index = random.nextInt(max);
            Key key = new PrimitiveKey(tree.getKeySchema());
            key.setKeys(new Long[]{new Long(random.nextInt(5)), new Long(random.nextInt(5)), new Long(index)});
            int option = random.nextInt(2);
            if (option == 0) {
                Value value = tree.createValue();
                //value.setObject(10);
                value.set(0, key.toString());

                if (!keys.contains(key)) {
                    Long part1 = (Long)key.get(0);
                    if (part1==0)
                        partialKeys.add(key);
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
            System.out.println("key = new KeyPrimitive(tree.getKeySchema());");
            System.out.println("key.setKeys(new Long[]{new Long("+deletedKey.get(0)+"L),"+
                                                    "  new Long("+deletedKey.get(1)+"L),"+
                                                    "  new Long("+deletedKey.get(2)+ "L)});");
            System.out.println("deletedKeys.add(key);");
        }
        
        for (Key addedKey : keys) {
            System.out.println("key = new KeyPrimitive(tree.getKeySchema());");
            System.out.println("key.setKeys(new Long[]{new Long("+addedKey.get(0)+"L),"+
                                                    "  new Long("+addedKey.get(1)+"L),"+
                                                    "  new Long("+addedKey.get(2)+ "L)});");
            System.out.println("keys.add(key);");
        }
        

    }

    public void testHard(BPlusTreeFileGeneric tree) throws Exception {
        Random random = new Random();
        int max = 50;

        ArrayList<Key> keys = new ArrayList<>();
        ArrayList<String> operations = new ArrayList();
        ArrayList<Key> deletedKeys = new ArrayList();
        Key key = null;

        key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(0L),  new Long(11L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(2L),  new Long(12L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(2L),  new Long(45L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(4L),  new Long(27L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(1L),  new Long(2L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(4L),  new Long(16L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(3L),  new Long(5L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(3L),  new Long(37L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(2L),  new Long(17L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(0L),  new Long(33L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(0L),  new Long(25L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(0L),  new Long(5L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(0L),  new Long(29L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(2L),  new Long(46L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(1L),  new Long(9L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(1L),  new Long(42L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(2L),  new Long(41L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(0L),  new Long(48L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(4L),  new Long(6L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(3L),  new Long(29L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(0L),  new Long(43L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(3L),  new Long(37L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(0L),  new Long(49L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(4L),  new Long(17L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(1L),  new Long(29L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(3L),  new Long(5L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(0L),  new Long(30L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(4L),  new Long(32L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(2L),  new Long(19L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(1L),  new Long(14L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(1L),  new Long(42L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(2L),  new Long(5L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(3L),  new Long(47L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(1L),  new Long(13L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(0L),  new Long(32L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(1L),  new Long(48L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(1L),  new Long(19L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(1L),  new Long(1L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(1L),  new Long(18L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(4L),  new Long(23L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(2L),  new Long(37L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(3L),  new Long(47L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(2L),  new Long(45L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(4L),  new Long(15L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(2L),  new Long(37L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(3L),  new Long(49L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(0L),  new Long(36L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(4L),  new Long(43L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(0L),  new Long(8L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(0L),  new Long(2L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(2L),  new Long(27L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(2L),  new Long(49L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(0L),  new Long(25L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(2L),  new Long(35L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(1L),  new Long(25L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(3L),  new Long(31L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(2L),  new Long(37L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(2L),  new Long(36L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(3L),  new Long(41L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(1L),  new Long(5L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(1L),  new Long(26L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(2L),  new Long(21L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(0L),  new Long(29L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(3L),  new Long(46L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(3L),  new Long(24L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(0L),  new Long(21L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(1L),  new Long(21L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(2L),  new Long(16L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(0L),  new Long(31L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(1L),  new Long(35L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(1L),  new Long(3L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(0L),  new Long(35L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(0L),  new Long(20L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(2L),  new Long(15L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(1L),  new Long(33L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(4L),  new Long(49L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(1L),  new Long(47L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(2L),  new Long(43L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(3L),  new Long(46L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(2L),  new Long(14L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(2L),  new Long(8L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(3L),  new Long(25L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(1L),  new Long(8L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(0L),  new Long(31L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(0L),  new Long(44L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(0L),  new Long(1L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(4L),  new Long(14L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(0L),  new Long(28L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(3L),  new Long(41L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(3L),  new Long(45L)});
deletedKeys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(0L),  new Long(0L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(1L),  new Long(1L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(4L),  new Long(2L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(0L),  new Long(3L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(2L),  new Long(4L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(2L),  new Long(5L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(0L),  new Long(6L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(4L),  new Long(7L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(3L),  new Long(8L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(1L),  new Long(9L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(4L),  new Long(10L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(1L),  new Long(11L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(4L),  new Long(13L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(4L),  new Long(14L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(1L),  new Long(15L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(3L),  new Long(16L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(3L),  new Long(17L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(3L),  new Long(18L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(3L),  new Long(19L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(3L),  new Long(20L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(0L),  new Long(21L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(3L),  new Long(22L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(3L),  new Long(23L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(2L),  new Long(24L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{2L,  new Long(3L),  new Long(25L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(2L),  new Long(26L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(3L),  new Long(27L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(2L),  new Long(28L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(0L),  new Long(30L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(2L),  new Long(31L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(0L),  new Long(27L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(3L),  new Long(33L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(2L),  new Long(24L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(4L),  new Long(0L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(0L),  new Long(37L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(4L),  new Long(21L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(4L),  new Long(32L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(3L),  new Long(20L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(0L),  new Long(7L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(1L),  new Long(17L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(0L),  new Long(34L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(0L),  new Long(39L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(3L),  new Long(28L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(0L),  new Long(30L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(3L),  new Long(13L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(4L),  new Long(19L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(2L),  new Long(38L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(0L),  new Long(24L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(0L),  new Long(32L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(0L),  new Long(12L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(1L),  new Long(35L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(1L),  new Long(21L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(2L),  new Long(7L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(0L),  new Long(27L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(0L),  new Long(20L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(4L),  new Long(1L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(4L),  new Long(21L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(1L),  new Long(37L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(1L),  new Long(39L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(2L),  new Long(43L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(4L),  new Long(48L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(0L),  new Long(36L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(2L),  new Long(30L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(4L),  new Long(3L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(2L),  new Long(30L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(3L),  new Long(24L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(3L),  new Long(27L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(2L),  new Long(35L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(2L),  new Long(47L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(1L),  new Long(46L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(0L),  new Long(18L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(2L),  new Long(41L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(0L),  new Long(13L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(1L),  new Long(4L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(4L),  new Long(31L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(1L),  new Long(46L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(3L),  new Long(34L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(3L),  new Long(32L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(4L),  new Long(44L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(2L),  new Long(27L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(4L),  new Long(22L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(3L),  new Long(36L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(2L),  new Long(15L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(2L),  new Long(41L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(3L),  new Long(43L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(3L),  new Long(2L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(1L),  new Long(33L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(3L),  new Long(45L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(3L),  new Long(9L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(1L),  new Long(9L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(3L),  new Long(2L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(2L),  new Long(4L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(0L),  new Long(7L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(4L),  new Long(5L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(1L),  new Long(41L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(4L),  new Long(16L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(0L),  new Long(26L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(1L),  new Long(8L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(2L),  new Long(38L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(0L),  new Long(26L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(3L),  new Long(26L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(2L),  new Long(32L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(2L),  new Long(25L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(3L),  new Long(30L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(2L),  new Long(5L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(1L),  new Long(23L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(3L),  new Long(48L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(1L),  new Long(49L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(4L),  new Long(27L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(0L),  new Long(4L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(2L),  new Long(30L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(4L),  new Long(10L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(3L),  new Long(3L),  new Long(49L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(2L),  new Long(27L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(2L),  new Long(24L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(2L),  new Long(41L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(1L),  new Long(40L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(4L),  new Long(11L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(3L),  new Long(48L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(0L),  new Long(45L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(1L),  new Long(42L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(1L),  new Long(4L),  new Long(33L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(2L),  new Long(1L),  new Long(9L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(2L),  new Long(13L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(4L),  new Long(4L),  new Long(3L)});
keys.add(key);
key = new PrimitiveKey(tree.getKeySchema());
key.setKeys(new Long[]{new Long(0L),  new Long(4L),  new Long(35L)});
keys.add(key);
        query4(tree, keys, operations);
        deletedQuery4(tree, deletedKeys, operations);

    }

    public void testHard2(BPlusTreeFileGeneric tree) throws Exception {
        Random random = new Random();
        int max = 3200;

        ArrayList<Key> keys = createList(tree.getKeySchema(), 0, 3200, false, false);
        insert4(tree, keys);

        ArrayList<String> operations = new ArrayList();

        ArrayList<Key> deletedKeys = new ArrayList();
        for (int i = 3200; i < max; i++) {
            Key key = new PrimitiveKey(tree.getKeySchema());
            key.setKeys(new Long[]{new Long(0), new Long(0), new Long(i)});
            deletedKeys.add(key);
        }

        while (true) {

            int option = random.nextInt(2);
            if (option == 0) {
                for (int i = 0; i < 10; i++) {
                    int index = random.nextInt(max);
                    Key key = new PrimitiveKey(tree.getKeySchema());
                    key.setKeys(new Long[]{new Long(0), new Long(0), new Long(index)});
                    Value value = tree.createValue();
                    //value.setObject(10);
                    value.set(0, "xx");
                    //System.out.println("insert "+key.toString());
                    if (!keys.contains(key)) {
                        operations.add("operations.add(\"insert," + key.get(2) + "\");");
                        tree.insert(key, value, true);
                        keys.add(key);
                        deletedKeys.remove(key);
                    }

                }

            } else {
                for (int i = 0; i < 10; i++) {
                    int index = random.nextInt(max);
                    Key key = new PrimitiveKey(tree.getKeySchema());
                    key.setKeys(new Long[]{new Long(0), new Long(0), new Long(index)});
                    //System.out.println("insert "+key.toString());

                    if (!deletedKeys.contains(key)) {
                        operations.add("operations.add(\"delete," + key.get(2) + "\");");
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

    public void testHard1(BPlusTreeFileGeneric tree, ArrayList<String> operations) throws Exception {
        Random random = new Random();
        int max = 50;

        ArrayList<Key> keys = createList(tree.getKeySchema(), 0, 32, false, false);
        insert4(tree, keys);

        ArrayList<Key> deletedKeys = new ArrayList();
        for (int i = 32; i < max; i++) {
            Key key = new PrimitiveKey(tree.getKeySchema());
            key.setKeys(new Long[]{new Long(0), new Long(0), new Long(i)});
            deletedKeys.add(key);
        }

        for (int i = 0; i < operations.size(); i++) {
            String s = operations.get(i);

            if (i == operations.size() - 1) {
                System.out.println("aqui");
            }
            String parts[] = s.split(",");
            Key key = new PrimitiveKey(tree.getKeySchema());
            key.setKeys(new Long[]{new Long(0), new Long(0), Long.valueOf(parts[1])});
            if (parts[0].equals("insert")) {
                Value value = tree.createValue();
                //value.setObject(10);
                value.set(0, "yy");
                System.out.println(i + " insert " + key.toString());
                tree.insert(key, value, true);
                keys.add(key);
                deletedKeys.remove(key);

            } else {
                System.out.println(i + " remove " + key.toString());

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

            RowSchema keyPrototype = new RowSchema(3);
            keyPrototype.addLongDataType();
            keyPrototype.addLongDataType();
            keyPrototype.addLongDataType();
            RowSchema valuePrototype = new RowSchema(1);
            valuePrototype.addStringDataType();
            PersistentPageFile p = new PersistentPageFile(4096, Paths.get("c:\\teste\\mtree\\mtree"), true);
            BPlusTreeFileGeneric tree = new BPlusTreeFileGeneric(p, keyPrototype, valuePrototype);
            //tree.initialize();
            //bplustree1 tree = new bplustree1(204, 254);

            Test_11 test = new Test_11();
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
            //test.delete(tree, new KeyPrimitive(0,0,35));
            //test.delete(tree, new KeyPrimitive(0,0,34));
            //test.delete(tree, new KeyPrimitive(0,0,33));
            //test.delete(tree, new KeyPrimitive(0,0,32));
            //test.delete(tree, new KeyPrimitive(0,0,31));
            //tree.printTree();
            //test.delete(tree, new KeyPrimitive(0,0,30));
            //tree.printTree();
            //test.delete(tree, new KeyPrimitive(0,0,29));
            //tree.printTree();
            //tree.printTree();
            //test.insert4(tree, list2);
            //ArrayList<String> operations = test.createOperations();
            //operations.add("insert,40");

            //test.testHard(200, tree);
            
            RowSchema schema = new RowSchema(1);
            schema.addLongDataType();
            Key key = new PrimitiveKey(schema);
            key.setKeys(new Comparable[]{new Long(0)});
            ArrayList<Value> values = tree.partialSearch(key);
            for (Value value : values) {
                System.out.println(value.toString());
            }
            //test.testHard(tree);
            test.testHard2(tree);

            tree.close();
        } catch (Exception ex) {
            //Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

}
