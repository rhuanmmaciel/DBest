/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table;

import ibd.table.btree.BTreeTable;

import java.io.File;
import java.util.Hashtable;
import ibd.table.prototype.Prototype;

/**
 * This class is the single point for creating instances of table. It assures a
 * single instance is created for every file. It is also useful for defining the
 * type of table that needs to be instantiated.
 *
 * @author Sergio
 */
public class Directory {

    static Hashtable<String, Table> tables = new Hashtable<String, Table>();

    public static Table getTable(String folder, String name, Prototype prototype, int cacheSize, int pageSize, boolean override) throws Exception {
        String key = folder + File.separator + name;
        Table t = tables.get(key);
        if (t != null && !override) {
            return t;
        }
        //t = new HeapTable(folder, name);
        //t = new BTreeTable(folder, name);
        t = new BTreeTable(null, folder, name,cacheSize);
        //t = new ChainedBlocksTable1(folder, name);

        if (override) {
            t.create(prototype, pageSize);
        } else {
            t.open();
        }

        t.tableKey = key;
        tables.put(key, t);
        return t;

    }

    public static Table getTable(String key, Prototype prototype, int cacheSize, int pageSize, boolean override) throws Exception {
        String folder = getTableFolder(key);
        String file = getTableFile(key);
        return Directory.getTable(folder, file, prototype, cacheSize, pageSize, override);
    }

    public static String getTableFolder(String key) {

        return key.substring(0, key.lastIndexOf(File.separator));

    }

    public static String getTableFile(String key) {

        return key.substring(key.lastIndexOf(File.separator), key.length());

    }

}
