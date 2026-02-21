/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ibd.transaction.instruction;

import ibd.table.Table;
import ibd.table.prototype.BasicDataRow;
import ibd.transaction.SimulatedIterations;
import java.util.ArrayList;
import java.util.List;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.LinkedDataRow;

/**
 *
 * @author pccli
 */
public class SingleUpdateInstruction extends Instruction{
    
    
    private String content;
    
    public SingleUpdateInstruction(Table table, int pk, String content) throws Exception{
        super(table, pk);
        this.content = content;
        mode = Instruction.UPDATE;
    }
    
    public SingleUpdateInstruction(Table table, String pk, String content) throws Exception{
        this(table, SimulatedIterations.getValue(pk), content);
    }
    
    
    
    @Override
    public List<DataRow> process() throws Exception{
        BasicDataRow rowData = new BasicDataRow();
        rowData.setInt("id", pk);
        LinkedDataRow rec = getTable().getRecord(rowData);
        if (transaction.getLogger()!=null)
            transaction.getLogger().transactionWrite(transaction,getTable(), pk, rec.toString(), content);
        
        endProcessing = true;
        List l = new ArrayList();
        rec.setString("nome", content);
        getTable().updateRecord(rec);
        l.add(rec);
        return l;
        
    }
    
    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    
}
