/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.persistent.cache;

import ibd.table.Directory;
import ibd.table.btree.BTreeTable;
import ibd.table.DataFaker;
import ibd.table.Utils;
import ibd.table.Params;
import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Random;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.StringColumn;

/**
 *
 * @author Sergio
 */
public class Main1 {

    public long execMultipleInsertions(Table table, int amount, boolean ordered, boolean display) throws Exception {

        Integer[] array = new Integer[amount];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }

        if (!ordered) {
            DataFaker.shuffleArray(array);
        }

        long start = System.currentTimeMillis();
        for (int i = 0; i < array.length; i++) {
            if (display) {
                System.out.println("adding primary key =  " + array[i]);
            }
            BasicDataRow rowData = new BasicDataRow();
            rowData.setInt("id", array[i]);
            rowData.setString("nome", "Novo registros " + array[i]);
            table.addRecord(rowData, true);

        }

        table.flushDB();
        long end = System.currentTimeMillis();
        return (end - start);
    }

    public Integer[] generateRecordIDs(int blocksAmount1, int blocksAmount2,
            int recordsAmount1, int recordsAmount2, int recordsCount) {
        ArrayList<Integer> list = new ArrayList();
        Random r = new Random();

        int min = 0;
        int max = blocksAmount1;

        for (int j = 0; j < recordsAmount1; j++) {
            int v = (int) (min + r.nextInt(max));
            list.add(v * recordsCount);
        }

        min = max;
        max = min + blocksAmount2;

        for (int j = 0; j < recordsAmount2; j++) {
            int v = (int) (min + r.nextInt(max - min));
            list.add(v * recordsCount);
        }

        Integer array[] = list.toArray(new Integer[list.size()]);

        DataFaker.shuffleArray(array);

        return array;
    }

    public long execMultipleSearches(Table table, Integer[] recIDs, boolean display) throws Exception {

        long start = System.currentTimeMillis();

        for (int i = 0; i < recIDs.length; i++) {
            BasicDataRow rowData = new BasicDataRow();
            rowData.setInt("id", recIDs[i]);
            DataRow rec = table.getRecord(rowData);

            if (rec != null) {
                if (!display) {
                    continue;
                }

                System.out.println(rec.toString());
            } else {
                System.out.println("erro: inexistente " + recIDs[i]);
            }
        }
        long end = System.currentTimeMillis();
        return (end - start);
    }

    public void test(Table table1, Integer[] recIDs) throws Exception {

        //table1.bufferManager = bufMan;
        Params.BLOCKS_LOADED = 0;
        long time = execMultipleSearches(table1, recIDs, false);
        System.out.println("BLOCKS_LOADED " + Params.BLOCKS_LOADED);
        System.out.println("time " + time);
    }

    public static void main(String[] args) {
        try {
            Main1 m = new Main1();
            
            Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("id", true));
        pt.addColumn(new StringColumn("nome"));
        
            Table table1 = Directory.getTable("c:\\teste\\ibd", "table.ibd", pt, 9999, Table.DEFULT_PAGE_SIZE,  true);
            m.execMultipleInsertions(table1, 10000, true, false);

            int recordsCount = 10;//table1.getRecordsAmount();

            Integer[] recIDs = m.generateRecordIDs(6, 60, 1000, 100, recordsCount);
            //Long[] recIDs = new Long[]{10L*31,11L*31,12L*31,13L*31,14L*31,15L*31,16L*31,1L*31,2L*31,3L*31,1L*31,2L*31,3L*31};

            System.out.println("LRU");
            table1 = new BTreeTable(null, "c:\\teste\\ibd", "table.ibd",99900000); 
//            {
//                @Override
//               public Cache defineBufferManagement(PersistentPageFile file) {
//            return new LRUCache(5000000, file);
//            }
//            };
            table1.open();
            m.test(table1, recIDs);

            System.out.println("MidPointBufferManager");
            table1 = new BTreeTable(null, "c:\\teste\\ibd", "table.ibd",99900000);
//            {
//               @Override
//               public Cache defineBufferManagement(PersistentPageFile file) {
//            return new MidPointCache(5000000, file);
//            }
//            };
            table1.open();
            m.test(table1, recIDs);

            System.out.println("MidPointBufferManager 2");
            table1 = new BTreeTable(null, "c:\\teste\\ibd", "table.ibd",99900000);
//            {
//               @Override
//               public Cache defineBufferManagement(PersistentPageFile file) {
//            return new MidPointCache2(5000000, file);
//            }
//            };
            table1.open();
            m.test(table1, recIDs);

            //m.execMultipleInsertions(table1, (int) (Block.RECORDS_AMOUNT * Table.BLOCKS_AMOUNT), true, false);
        } catch (Exception ex) {
            Logger.getLogger(Main1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}




