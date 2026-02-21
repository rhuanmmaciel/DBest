/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.table;

import engine.exceptions.DataBaseException;
import ibd.query.lookup.LookupFilter;
import ibd.table.lookup.RowLookupFilter;
import ibd.table.prototype.BasicDataRow;
import ibd.table.prototype.Header;
import java.util.List;
import ibd.table.prototype.LinkedDataRow;
import ibd.table.prototype.Prototype;
import ibd.table.prototype.column.BooleanColumn;
import ibd.table.prototype.column.Column;
import ibd.table.prototype.column.DoubleColumn;
import ibd.table.prototype.column.FloatColumn;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.LongColumn;
import ibd.table.prototype.column.StringColumn;
import java.io.IOException;
import java.util.Iterator;

public abstract class Table  {

    public static final int DEFULT_PAGE_SIZE = 4096;

    public String tableKey;
    
    protected Header header;
    
    protected Prototype prototype;
    
    public Table(Header header)  {
        this.header = header;
        if (header!=null)
            prototype = convertPrototype();
        
        
    }
    
    private Prototype convertPrototype() {
        
        Prototype prot = header.getPrototype();
        if (prot==null) return null;
        
        Prototype newProt = new Prototype();

        for (Column c : prot) {
            
            newProt.addColumn(convertColumn(c));
        }
        return newProt;
    }
    
    public static Column convertColumn(Column col) {
        Column newCol;

        switch (col.getType()) {
            case "STRING":
                newCol = new StringColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            case "INTEGER":
                newCol = new IntegerColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            case "LONG":
                newCol = new LongColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            case "FLOAT":
                newCol = new FloatColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            case "DOUBLE":
                newCol = new DoubleColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            case "BOOLEAN":
                newCol = new BooleanColumn(col.getName(), col.getSize(), col.getFlags());
                break;
            default:
                throw new AssertionError();
        }

        return newCol;
    }
    
    public String saveHeader(String path) {
        try {
            return header.save(path);
        }catch (IOException ex){
            throw new DataBaseException("Table->saveHeader",ex.getMessage());
        }
    }

    /*
        Retorna o objeto Header da table
     */
    public Header getHeader(){
        return header;
    }
    
    public abstract String getName();
    public abstract String getHeaderName();

    public abstract LinkedDataRow getRecord(BasicDataRow rowdata) throws Exception;

    public abstract List<LinkedDataRow> getRecords(String col, Comparable comp, int comparisonType) throws Exception;
    
    public abstract List<LinkedDataRow> getAllRecords() throws Exception;
    
    public abstract List<LinkedDataRow> getRecords(BasicDataRow rowData)throws Exception;

    public abstract LinkedDataRow addRecord(BasicDataRow rowdata, boolean unique) throws Exception;

    public abstract LinkedDataRow updateRecord(BasicDataRow rowdata) throws Exception;
    
    public abstract LinkedDataRow updateRecord(LinkedDataRow rowdata) throws Exception;
        
    public abstract LinkedDataRow removeRecord(BasicDataRow rowdata) throws Exception;

    public abstract void flushDB() throws Exception;

    public abstract int getRecordsAmount() throws Exception;
    
    public abstract void printStats() throws Exception;
    
    public abstract void open() throws Exception;
    
    public abstract void create(Prototype prototype, int pageSize) throws Exception;
    
    public abstract void close() throws Exception;
    
    public abstract Prototype getPrototype();
    
    public abstract LinkedDataRow getRecord(LinkedDataRow rowdata) throws Exception;
    
    public abstract List<LinkedDataRow> getRecords(LinkedDataRow rowData)throws Exception;
    
    public abstract boolean contains(LinkedDataRow pkRow);
    
    public abstract List<LinkedDataRow> getFilteredRecords(LookupFilter filter) throws Exception;
    
    public abstract Iterator getAllRecordsIterator() throws Exception;
    public abstract Iterator getFilteredRecordsIterator(LookupFilter filter) throws Exception;
    public abstract List<LinkedDataRow> getRecords(LinkedDataRow pkRow, LookupFilter rowFilter)throws Exception;
    public abstract Iterator getPKFilteredRecordsIterator(LinkedDataRow pkRow, LookupFilter rowFilter, int compType) throws Exception ;
}
