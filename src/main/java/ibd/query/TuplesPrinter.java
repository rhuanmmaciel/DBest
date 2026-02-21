/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ibd.query;

import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.column.Column;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author ferna
 */
public class TuplesPrinter {
    
    public void execQueryAndPrint(Operation query, int tuplesToRead) throws Exception {
        if (tuplesToRead>0){
            System.out.println("Set to retrieve first "+tuplesToRead+ " rows");
            query.setPageInfo(0, tuplesToRead);
        }
        else System.out.println("Set to retrieve all rows");
        
        Stats.passes = 0;
        Iterator<Tuple> tuples = query.run();
        List<Tuple> list = new ArrayList();
        long start = System.currentTimeMillis();
        while (tuples.hasNext()) {
            list.add(tuples.next());
        }
        long end = System.currentTimeMillis();
        
        int count = 0;
        boolean firstTime = true;
        int lineSize = 0;
        
        
        for (Tuple tuple : list) {
//            if (firstTime){
//                lineSize = getLineSize(tuple);
//                printLine(lineSize);
//                printSchema(tuple);
//            }
            printRow(tuple);
//            firstTime = false;
            //System.out.println(count+":"+tuple);
            count++;
//                if (count > 100) {
//                    break;
//                }
        }
        
        printLine(lineSize);
        double time = ((double)(end-start))/1000;
        System.out.println("Found " + count+" rows in "+time + " secs");
        //System.out.println("passes "+Stats.passes);
    }
    
    
    private String printPaddedString(String inputString, int desiredSize) {
        // Calculate the number of spaces needed for padding
        int paddingSize = Math.max(0, desiredSize - inputString.length());

        // Pad the string with spaces to reach the desired size
        StringBuilder paddedString = new StringBuilder(inputString);
        for (int i = 0; i < paddingSize; i++) {
            paddedString.append(" ");
        }

        // Print the padded string
        return paddedString.toString();
    }
    
    private void printLine(int paddingSize) {
        // Calculate the number of spaces needed for padding
        // Pad the string with spaces to reach the desired size
        StringBuilder paddedString = new StringBuilder("");
        for (int i = 0; i < paddingSize; i++) {
            paddedString.append("-");
        }

        // Print the padded string
        System.out.println(paddedString.toString());
    }
    
    private void printSchema(Tuple tuple){
        String line = "|";
        String line2 = "|";
        for (LinkedDataRow row : tuple.rows) {
            for (Column column : row.getPrototype().getColumns()) {
                  line+=printPaddedString(column.getName(), column.getSize())+"|";
            }
        }
        System.out.println(line);
    }
    
    private int getLineSize(Tuple tuple){
        int size = 1;
        for (LinkedDataRow row : tuple.rows) {
            List<Column> columns = row.getPrototype().getColumns();
            for (int i = 0; i < columns.size(); i++) {
                  Column column = columns.get(i);
                  size += 1+(column.getSize()>column.getName().length()?column.getSize():column.getName().length());
            }
        }
        return size;
    }
    
    private void printRow(Tuple tuple){
        String line = "|";
        for (LinkedDataRow row : tuple.rows) {
            List<Column> columns = row.getPrototype().getColumns();
            for (int i = 0; i < columns.size(); i++) {
                  Column column = columns.get(i);
                  
                  String content = row.getValue(i).toString();
                  int size = (column.getSize()>column.getName().length()?column.getSize():column.getName().length());
                  line+=printPaddedString(content, size)+"|";
            }
        }
        System.out.println(line);
    }

    
}
