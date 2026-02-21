/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.transaction.instruction;

import ibd.table.ComparisonTypes;
import ibd.query.Operation;
import ibd.query.Tuple;
import ibd.query.lookup.ColumnElement;
import ibd.query.lookup.LiteralElement;
import ibd.query.lookup.SingleColumnLookupFilter;
import ibd.query.sourceop.FullTableScan;
import ibd.query.unaryop.filter.Filter;
import ibd.table.Table;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ibd.table.prototype.DataRow;

/**
 *
 * @author pccli
 */
public class MultiReadInstruction extends Instruction {

    int comparisonType;
    int comparisonType2 = -1;
    int pk2;
    
    Operation query;

    public MultiReadInstruction(Table table, int pk, int comparisonType) throws Exception {
        super(table, pk);
        this.comparisonType = comparisonType;
        mode = Instruction.READ;
    }
    
    public MultiReadInstruction(Table table, int pk, int comparisonType, int pk2, int comparisonType2) throws Exception {
        this(table, pk, comparisonType);
        this.comparisonType2 = comparisonType2;
        this.pk2 = pk2;
    }

    public int getLastPK(){
        return pk2;
    }
    /*
    public List<AbstractRecord> processX() throws Exception {
        List l = new ArrayList();
        if (query==null){
            Operation scan1 = new TableScan("t1", getTable());
            if (comparisonType2==-1)
                query = new PKFilter(scan1, "t1", comparisonType, pk);
            else query = new PkRangeFilter(scan1, "t1", comparisonType, pk, comparisonType2, pk2); 
            query.open();
        }
        
        if (query.hasNext()){
            
            Tuple t = query.next();
            l.add(t.sourceTuples[0].record);
        }
        if (!query.hasNext()){
            endProcessing = true;
            query.close();
        }

        return l;
    }
    */
    
    @Override
    public List<DataRow> process() throws Exception {
        List l = new ArrayList();

        Operation scan1 = new FullTableScan("t1", getTable());
        if (comparisonType2==-1){
            SingleColumnLookupFilter filter_1 = new SingleColumnLookupFilter(new ColumnElement("id"), comparisonType, new LiteralElement(pk));
            query = new Filter(scan1, filter_1);
        }
        //else query = new ColumnRangeFilter(scan1, "t1", "id", comparisonType, pk, comparisonType2, pk2); 

        Iterator<Tuple> it = query.run();
        while (it.hasNext()) {
            Tuple t = it.next();
            l.add(t.rows[0]);
        }
        query.close();
        endProcessing = true;
        return l;
    }

    @Override
    public int getMode() {
        return Instruction.READ;
    }

    @Override
    public String getUniqueKey() {
        return getTable().tableKey + " " + ComparisonTypes.getComparisonType(comparisonType) + " " + pk;
    }

}
