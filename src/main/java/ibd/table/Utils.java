/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table;

import ibd.table.prototype.BasicDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.StringColumn;

/**
 *
 * @author pccli
 */
public class Utils {

   

    static public Table createTable(String folder, String name, int pageSize, int size, boolean shuffled, int range, int cardinality) throws Exception {

        Prototype pt = new Prototype();
        pt.addColumn(new IntegerColumn("id", true));
        pt.addColumn(new StringColumn("nome"));
        pt.addColumn(new IntegerColumn("idade"));
        pt.addColumn(new StringColumn("cidade"));

        Table table = Directory.getTable(folder, name,pt, 99999,  pageSize, true);
        //table.initLoad();

        Integer[] array1 = new Integer[(int) Math.ceil((double) size / range)];
        for (int i = 0; i < array1.length; i++) {
            array1[i] = i * range;
        }

        if (shuffled) {
            DataFaker.shuffleArray(array1);
        }

        String names_[] = DataFaker.generateNames(cardinality, array1.length);
        
        String cidades[] = DataFaker.generateStrings(new String[]{"Santa Maria", "Porto Alegre","Sao Paulo"}, array1.length, true);

        //Integer idades[] = DataFaker.generateInts(20,array1.length, 20, 2, true );
        Integer idades[] = new Integer[array1.length];
        
        
        int groupSize = 1 + array1.length/3;
        int startValue = 20;
        int gap = 20;
        int offset = 0;
        for (int i = 0; i < 3; i++) {
            Integer idades_[] = DataFaker.generateInts(20,groupSize, startValue, 2, true );
            System.arraycopy(idades_, 0, idades, offset, idades_.length);
            startValue+=gap;
            offset+=idades_.length;
            if (offset+groupSize>idades.length)
                groupSize = idades.length-offset;
        }
        
        
        for (int i = 0; i < array1.length; i++) {
            //String text = name + "(" + array1[i] + ")";
            String text = names_[i];
            //text = Utils.pad(text, 40);
            BasicDataRow row = new BasicDataRow();
            row.setInt("id", array1[i]);
            row.setString("nome", text);
            row.setInt("idade", idades[i]);
            row.setString("cidade", cidades[i]);
            table.addRecord(row, true);

            //table.addRecord(array1[i], String.valueOf(array1[i]));
            //table.addRecord(array1[i], "0");
        }
        table.flushDB();
        return table;
    }
    

}
